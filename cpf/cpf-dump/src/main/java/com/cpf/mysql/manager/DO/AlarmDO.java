package com.cpf.mysql.manager.DO;

import com.cpf.constants.AlarmTypeEnum;
import com.cpf.influx.manager.DO.MonitorDO;
import lombok.Data;

import java.util.Date;

/**
 * @author jieping
 * @create 2018-04-28 11:01
 * @desc 报警信息
 **/
@Data
public class AlarmDO {
    private Long id;
    /**
     * 报警类型
     */
    private AlarmTypeEnum type;
    /**
     * 报警针对的数据
     */
    private MonitorDO monitorDO;
    /**
     * 报警触发的监控规则
     */
    private RuleDO ruleDO;
    /**
     * 报警时间
     */
    private Date time;
    /**
     * 报警是否失效
     */
    private Boolean expire;
}
