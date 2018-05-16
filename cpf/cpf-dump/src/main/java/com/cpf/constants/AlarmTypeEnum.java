package com.cpf.constants;

/**
 * @author jieping
 * @create 2018-04-28 10:51
 * @desc 报警类型
 **/
public enum  AlarmTypeEnum {
    /**
     * 根据算法模型进行预警
     */
    PREDICT("算法预警"),
    /**
     * 根据监控规则判断报警
     */
    MONITOR("监控报警");
    private String desc;
    AlarmTypeEnum(String desc){
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
