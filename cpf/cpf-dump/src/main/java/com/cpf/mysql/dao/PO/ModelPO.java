package com.cpf.mysql.dao.PO;

import lombok.Data;

import javax.persistence.*;
/**
 * @author jieping
 * @create 2018-04-25 19:56
 * @desc 算法模型
 **/
@Data
@Entity(name = "model")
public class ModelPO {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    /**
     * 模型占决策的比重
     */
    private Double weight = 0D;
    /**
     * 模型参数配置
     */
    @OneToOne(cascade = CascadeType.ALL)
    private ModelOptionsPO config;
    /**
     * 模型正确率
     */
    private Double correctRate;
}
