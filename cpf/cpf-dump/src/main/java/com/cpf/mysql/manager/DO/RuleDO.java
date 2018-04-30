package com.cpf.mysql.manager.DO;

import lombok.Data;

import java.util.Date;
import java.util.Map;
/**
 * @author jieping
 * @create 2018-04-18
 **/
@Data
public class RuleDO {
    private Long id;
    /**
     * 监控规则名称
     */
    private String name;
    /**
     * 监控类型
     */
    private String type;
    /**
     * 规则配置
     */
    private Map<String,String> config;
    /**
     * 规则创建时间
     */
    private Date createTime = new Date();
    /**
     * 规则修改时间
     */
    private Date modifyTime;
    /**
     * 持续时间
     */
    private Long time;
}
