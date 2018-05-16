package com.cpf.mysql.dao.PO;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import java.util.Date;

/**
 * @author jieping
 * @create 2018-04-28 10:50
 * @desc 报警信息
 **/
@Data
@Entity(name = "alarm")
public class AlarmPO {
    @Id
    @GeneratedValue
    private Long id;
    /**
     * 报警类型
     */
    private String type;
    /**
     * 报警针对的数据
     */
    @Lob
    private String data;
    /**
     * 报警触发的监控规则，当报警类型为监控报警时使用
     */
    @Lob
    private String rule;
    /**
     * 报警时间
     */
    private Date time;
    /**
     * 报警是否失效
     */
    private Boolean expire;
}
