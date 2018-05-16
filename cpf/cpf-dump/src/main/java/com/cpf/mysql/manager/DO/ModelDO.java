package com.cpf.mysql.manager.DO;

import lombok.Data;

import javax.persistence.OneToOne;
/**
 * @author jieping
 * @create  2018-04-09
 **/
@Data
public class ModelDO {
    private Long id;
    private String name;
    /**
     * 模型占决策的比重
     */
    private Double weight =0D;
    /**
     * 模型参数配置
     */
    @OneToOne
    private ModelOptionsDO config;
    /**
     * 模型正确率
     */
    private Double correctRate;
}
