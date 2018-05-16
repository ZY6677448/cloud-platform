package com.cpf.constants;
/**
 * @author jieping
 * @create 2018-04-25 19:56
 * @desc 模型参数类型
 **/
public enum OptionTypeEnum {
    INTEGER("integer","整数"),
    DOUBLE("double","浮点数"),
    ENUM("enum","枚举"),
    BOOLEAN("bool","是否");
    /**
     * 类型
     */
    private String type;
    /**
     * 描述
     */
    private String desc;
    OptionTypeEnum(String type,String desc){
        this.type = type;
        this.desc = desc;
    }

    public String getType() {
        return type;
    }
    public String getDesc() {
        return desc;
    }
}
