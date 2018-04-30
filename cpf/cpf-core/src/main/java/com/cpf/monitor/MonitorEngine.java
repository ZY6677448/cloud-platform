package com.cpf.monitor;

import com.alibaba.fastjson.JSON;
import com.cpf.constants.AlarmTypeEnum;
import com.cpf.influx.manager.DO.MonitorDO;
import com.cpf.influx.manager.MonitorManager;
import com.cpf.mysql.manager.AlarmManager;
import com.cpf.mysql.manager.DO.AlarmDO;
import com.cpf.mysql.manager.DO.RuleDO;
import com.cpf.logger.BusinessLogger;
import com.cpf.service.CallbackResult;
import com.cpf.service.ServiceExecuteTemplate;
import com.cpf.service.ServiceTemplate;
import com.cpf.utils.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
/**
 * @author jieping
 * @create 2018-04-19
 * @desc 实时监控引擎
 **/
@Component
public class MonitorEngine extends ServiceTemplate{
    private Logger logger = LoggerFactory.getLogger(MonitorEngine.class);
    @Autowired
    private RuleHolder ruleHolder;
    @Autowired
    private MonitorManager monitorManager;
    @Autowired
    private AlarmManager alarmManager;
    private static final String CLASS_TAG = "danger";
    private static final Boolean DANGER = true;
    private static final Boolean SAFE = false;

    /**
     * 实时监控，先判断该时刻数据是否满足监控规则，满足则判断监控规则规定的时间段内 监控数据平均值是否满足监控规则，满足则报警
     * @param monitorDO 实时监控的数据
     * @return 危险为 true 安全为false
     */
    public boolean monitor(MonitorDO monitorDO){
        Object tempResult =  execute(logger, "monitor", new ServiceExecuteTemplate() {
            @Override
            public CallbackResult<Object> checkParams() {
                if(monitorDO == null){
                    return CallbackResult.failure();
                }else {
                    return CallbackResult.success();
                }
            }

            @Override
            public CallbackResult<Object> executeAction() {
                CallbackResult<Object> result = new CallbackResult<Object>(SAFE,true);
                List<RuleDO> ruleDOList = ruleHolder.getRules(monitorDO.getType());
                for(RuleDO ruleDO : ruleDOList){
                    //判断该时刻是否满足监控规则
                    if(verify(monitorDO,ruleDO)){
                        //如果监控规则有规定持续时间
                        if(ValidationUtil.isNotNull(ruleDO.getTime())){
                            //判断一定时间内的平均值是否满足监控规则
                            MonitorDO meanMonitor = monitorManager.queryAVGByTime(monitorDO,ruleDO.getTime());
                            if(verify(meanMonitor,ruleDO)){
                                monitorDO.getData().put(CLASS_TAG,"true");
                                result.setResult(DANGER);
                                warn(meanMonitor,ruleDO);
                            }else {
                                monitorDO.getData().put(CLASS_TAG,"false");
                            }
                        }
                        monitorManager.addMonitor(monitorDO);
                    }
                }
                return result;
            }
        });
        CallbackResult<Boolean> result = (CallbackResult<Boolean>)tempResult;
        return result.getResult();
    }

    /**
     * 判断监控数据是否命中规则
     * @param monitorDO
     * @param ruleDO
     * @return
     */
    private boolean verify(MonitorDO monitorDO,RuleDO ruleDO){
        boolean result = true;
        //遍历监控数据，判断数据是否满足监控规则
        for(Map.Entry<String,String> monitorEntry : monitorDO.getData().entrySet()){
            MonitorComparator comparator = ComparatorMap.getComparator(monitorEntry.getKey());
            if(comparator == null){
                //该属性不需要比较
                continue;
            }
            //监控数据没有满足 规则
            if(!comparator.compare(monitorEntry.getValue(),ruleDO.getConfig().get(monitorEntry.getKey()))){
                result = false;
                return result;
            }
        }
        return result;
    }

    /**
     * 报警
     * @param monitorDO
     * @param ruleDO
     */
    private void warn(MonitorDO monitorDO,RuleDO ruleDO){
        AlarmDO alarmDO = new AlarmDO();
        alarmDO.setType(AlarmTypeEnum.MONITOR);
        alarmDO.setMonitorDO(monitorDO);
        alarmDO.setRuleDO(ruleDO);
        alarmManager.save(alarmDO);
        BusinessLogger.errorLog("MonitorEngine.monitor",
                new String[]{JSON.toJSONString(monitorDO),JSON.toJSONString(ruleDO)},
                "MONITOR_DANGER","监控报警",logger);

    }
}
