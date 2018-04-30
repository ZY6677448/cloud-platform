package com.cpf.mysql.manager.DO;

import com.cpf.constants.OptionTypeEnum;
import lombok.Data;

import java.util.Map;
/**
 * @author jieping
 * @create 2018-04-09
 **/
@Data
public class ModelOptionDO {
    /**
     * 参数名称
     */
    private String key;
    /**
     * 参数描述
     */
    private String desc;
    /**
     * 参数值
     */
    private Object value ;
    /**
     * 参数值类型
     */
    private OptionTypeEnum valueType;
    /**
     * 扩展值，当数据类型为enum时表示enum的种类
     */
    private Map<String,String> extension;
}
