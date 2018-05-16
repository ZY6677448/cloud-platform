package com.cpf.holder;

import com.alibaba.fastjson.JSON;
import com.cpf.mysql.manager.DO.RuleDO;
import com.cpf.mysql.manager.RuleManager;
import com.cpf.logger.BusinessLogger;
import com.cpf.service.CallbackResult;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
/**
 * @author jieping
 * @create 2018-04-19
 * @desc 监控规则 内存持有类，将监控规则从数据库拿到内存中
 **/
@Component
public class RuleHolder {
    @Autowired
    private RuleManager ruleManager;
    private static final Logger logger = LoggerFactory.getLogger(RuleHolder.class);
    private  Map<String,List<RuleDO>> ruleMap = Maps.newHashMap();
    /**
     * 从数据库获取所有监控规则,替代现有规则,类初始化后会自动调用一次
     */
    @PostConstruct
    public  void refresh(){
        CallbackResult<List<RuleDO>> result = ruleManager.all();
        if(result.getSuccess()){
            Map<String,List<RuleDO>> tempMap = Maps.newHashMap();
            for(RuleDO ruleDO : result.getResult()){
                tempMap.computeIfAbsent(ruleDO.getType(), k -> Lists.newArrayList());
                tempMap.get(ruleDO.getType()).add(ruleDO);
            }
            ruleMap = tempMap;
            BusinessLogger.infoLog("RuleHolder.refresh",new String[]{JSON.toJSONString(ruleMap)},"success","监控规则刷新成功",logger);
        }else {
            BusinessLogger.errorLog("RuleHolder.refresh",new String[]{JSON.toJSONString(result)},"RULES_REFRESH_FAILED","监控规则刷新失败",logger);
        }
    }

    /**
     * 获取指定类型的监控规则
     * @param type
     * @return
     */
    public List<RuleDO> getRules(String type){
        ruleMap.computeIfAbsent(type, k -> Lists.newArrayList());
        return ruleMap.get(type);
    }
}
