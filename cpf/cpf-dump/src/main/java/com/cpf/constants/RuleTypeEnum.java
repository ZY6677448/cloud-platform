package com.cpf.constants;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author jieping
 * @create 2018-04-19
 * @desc 监控数据类型（对应influx的表名称）
 **/
public enum  RuleTypeEnum {

    CPU("win_cpu"),
    DISK("win_disk"),
    DISKIO("win_diskio"),
    MEM("win_mem"),
    NET("win_net"),
    PERF("win_perf_counters"),
    SWAP("win_swap"),
    SYSTEM("win_system"),
    SYSTEM_DAYS("win_system_days")
    ;
    /**
     * 监控规则类型，等于数据库表名
     */
    private String type;
    /**
     * 数据库表的tags
     */
    private static final List<String> MONITOR_DATA_TAG_LIST = Collections.unmodifiableList(Lists.newArrayList("host","instance","objectname"));
    private static final List<String> TRAIN_DATA_TAG_LIST = Collections.unmodifiableList(Lists.newArrayList("host","instance","objectname","danger"));
    RuleTypeEnum(String type){
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static List<String> getTagList(Boolean withDanger) {
        if(withDanger){
            return TRAIN_DATA_TAG_LIST;
        }else {
            return MONITOR_DATA_TAG_LIST;
        }
    }

    /**
     * 获取 前端展示格式的规则类型
     * @return
     */
    public static List<Map<String,String>> getViewOptions(){
        List<Map<String,String>> list = Lists.newArrayList();
        for(RuleTypeEnum ruleTypeEnum : RuleTypeEnum.values()){
            Map<String,String> map = Maps.newHashMap();
            map.put("value",ruleTypeEnum.getType());
            map.put("label",ruleTypeEnum.getType());
            list.add(map);
        }
        return list;
    }
    public static RuleTypeEnum typeOf(String type){
        for(RuleTypeEnum ruleTypeEnum : RuleTypeEnum.values()){
            if(ruleTypeEnum.getType().equals(type)){
                return ruleTypeEnum;
            }
        }
        return null;
    }
}
