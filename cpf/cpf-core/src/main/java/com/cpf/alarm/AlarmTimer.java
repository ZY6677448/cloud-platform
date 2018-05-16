package com.cpf.alarm;

import com.alibaba.fastjson.JSON;
import com.cpf.constants.AlarmTypeEnum;
import com.cpf.logger.BusinessLogger;
import com.cpf.mysql.manager.AlarmManager;
import com.cpf.mysql.manager.DO.AlarmDO;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author jieping
 * @create 2018-05-04 19:42
 * @desc 报警计时器，用于报警失效和撤销
 **/
@Component
public class AlarmTimer {
    private static final String SPLIT = "#";
    private static final Long EXPIRE_TIME = 60*60*1000L;
    private static final Logger logger = LoggerFactory.getLogger(AlarmTimer.class);
    @Autowired
    private AlarmManager alarmManager;
    /**
     * 报警失效时间map
     * 监控报警类型 key:alarmType + monitorType + hostName + ruleId
     * 算法预警类型 key:alarmType + monitorType + hostName
     * value ：过期时间
     */
    private Map<String,Long> expireMap = Maps.newHashMap();
    /**
     * 报警id map
     * 监控报警类型 key:alarmType + monitorType + ruleId
     * 算法预警类型 key:alarmType + monitorType
     * value ：数据库表alarm对应的id
     */
    private Map<String,Long> alarmMap = Maps.newHashMap();
    @PostConstruct
    private void init(){
        List<AlarmDO> alarmDOList = alarmManager.get(false).getResult();
        alarmDOList.forEach(this::put);
    }
    /**
     * 存放一个报警计时器
     * @param alarmDO
     */
    public void put(AlarmDO alarmDO){
        expireMap.put(key(alarmDO),alarmDO.getTime().getTime()+EXPIRE_TIME);
        alarmMap.put(key(alarmDO),alarmDO.getId());
    }


    /**
     * 定期清除报警计时器已经过期的报警
     */
    @Scheduled(fixedDelay = 60*1000L)
    private void remove(){
        Long currentTime = System.currentTimeMillis();
        for(Iterator<Map.Entry<String,Long>> it = expireMap.entrySet().iterator();it.hasNext();){
            Map.Entry<String, Long> entry = it.next();
            //如果报警已经过期
            if(entry.getValue() <= currentTime){
                //从报警计时map移除
                it.remove();
                //从报警id map 一处
                Long alarmId =  alarmMap.remove(entry.getKey());
                //将数据库报警置为已过期
                AlarmDO alarmDO = alarmManager.get(alarmId).getResult();
                alarmDO.setExpire(true);
                alarmManager.save(alarmDO);
                BusinessLogger.infoLog("alarmTimer.remove",
                        new String[]{JSON.toJSONString(alarmDO)},
                        "success",
                        "报警解除",
                        logger);

            }
        }
    }

    /**
     * 是否已经存在该报警了
     * @param alarmDO
     * @return
     */
    public boolean exist(AlarmDO alarmDO){
        return expireMap.containsKey(key(alarmDO));
    }
    /**
     * 生成报警计时器的key
     * 监控报警类型 key:alarmType + monitorType + hostName + ruleId
     * 算法预警类型 key:alarmType + monitorType + hostName
     * @param alarmDO
     * @return
     */
    private static String key(AlarmDO alarmDO){
        List<String> keys = Lists.newArrayList();
        keys.add(alarmDO.getType().toString());
        keys.add(alarmDO.getMonitorDO().getData().get("host"));
        keys.add(alarmDO.getMonitorDO().getType());
        if(AlarmTypeEnum.MONITOR==alarmDO.getType()){
            keys.add(alarmDO.getRuleDO().getId().toString());
        }
        return Joiner.on(SPLIT).join(keys);
    }
}
