package com.cpf.monitor;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.Map;
/**
 * @author jieping
 * @create 2018-04-19
 * @desc 监控数据比较器
 **/
public class ComparatorMap {
    private static Map<String,MonitorComparator> map = Maps.newHashMap();
    /**
     * 预期数据是否大于规则数据的double类型比较器
     */
    private static final MonitorComparator MAX_DOUBLE = ( monitorValue, ruleValue)->{
        //为空代表条件不限制
        if(StringUtils.isBlank(ruleValue) || StringUtils.isBlank(monitorValue)){
            return true;
        }else {
            return NumberUtils.toDouble(monitorValue) >= NumberUtils.toDouble(ruleValue);
        }
    };
    /**
     * 预期数据是否小于规则数据的double类型比较器
     */
    private static final MonitorComparator MIN_DOUBLE = ( monitorValue, ruleValue)->{
        //为空代表条件不限制
        if(StringUtils.isBlank(ruleValue) || StringUtils.isBlank(monitorValue)){
            return true;
        }else {
            return NumberUtils.toDouble(monitorValue) <= NumberUtils.toDouble(ruleValue);
        }
    };
    /**
     * 预期数据是否大于规则数据的long类型比较器
     */
    private static final MonitorComparator MAX_LONG = ( monitorValue, ruleValue)->{
        //为空代表条件不限制
        if(StringUtils.isBlank(ruleValue) || StringUtils.isBlank(monitorValue)){
            return true;
        }else {
            return NumberUtils.toLong(monitorValue) >= NumberUtils.toLong(ruleValue);
        }
    };
    /**
     * 预期数据是否小于规则数据的long类型比较器
     */
    private static final MonitorComparator MIN_LONG = ( monitorValue, ruleValue)->{
        //为空代表条件不限制
        if(StringUtils.isBlank(ruleValue) || StringUtils.isBlank(monitorValue)){
            return true;
        }else {
            return NumberUtils.toLong(monitorValue) <= NumberUtils.toLong(ruleValue);
        }
    };
    private static final MonitorComparator EQUAL_STRING = ( monitorValue, ruleValue)->{
        //为空代表条件不限制
        if(StringUtils.isBlank(ruleValue) || StringUtils.isBlank(monitorValue)){
            return true;
        }else {
            return monitorValue.equals(ruleValue);
        }
    };
    static{
        map.put("host",EQUAL_STRING);
        //win_cpu rule params comparator
        map.put("Percent_DPC_Time",MAX_DOUBLE);
        map.put("Percent_Interrupt_Time",MAX_DOUBLE);
        map.put("Percent_Privileged_Time",MAX_DOUBLE);
        map.put("Percent_Processor_Time",MAX_DOUBLE);
        map.put("Percent_User_Time",MAX_DOUBLE);
        //win_disk rule params comparator
        map.put("Free_Megabytes",MIN_LONG);
        map.put("Percent_Free_Space",MIN_DOUBLE);
        map.put("Percent_Idle_Time",MAX_DOUBLE);
        //win_disk_io rule params comparator
        map.put("Current_Disk_Queue_Length",MAX_LONG);
        map.put("Disk_Read_Bytes_persec",MAX_DOUBLE);
        map.put("Disk_Reads_persec",MAX_DOUBLE);
        map.put("Disk_Write_Bytes_persec",MAX_DOUBLE);
        map.put("Disk_Writes_persec",MAX_DOUBLE);
        map.put("Percent_Disk_Read_Time",MAX_DOUBLE);
        map.put("Percent_Disk_Time",MAX_DOUBLE);
        map.put("Percent_Disk_Write_Time",MAX_DOUBLE);
        //win_mem rule params comparator
        map.put("Available_Bytes",MIN_LONG);
        map.put("Cache_Faults_persec",MAX_DOUBLE);
        map.put("Demand_Zero_Faults_persec",MAX_DOUBLE);
        map.put("Page_Faults_persec",MAX_DOUBLE);
        map.put("Pages_persec",MAX_DOUBLE);
        map.put("Pool_Nonpaged_Bytes",MAX_LONG);
        map.put("Pool_Paged_Bytes",MAX_LONG);
        map.put("Standby_Cache_Core_Bytes",MAX_DOUBLE);
        map.put("Standby_Cache_Core_BytesStandby_Cache_Normal_Priority_Bytes",MAX_DOUBLE);
        map.put("Standby_Cache_Reserve_Bytes",MAX_DOUBLE);
        map.put("Transition_Faults_persec",MAX_DOUBLE);
        //win_net rule params comparator
        map.put("Bytes_Received_persec",MAX_DOUBLE);
        map.put("Bytes_Sent_persec",MAX_DOUBLE);
        map.put("Packets_Outbound_Discarded",MAX_DOUBLE);
        map.put("Packets_Outbound_Errors",MAX_LONG);
        map.put("Packets_Received_Discarded",MAX_DOUBLE);
        map.put("Packets_Received_Errors",MAX_LONG);
        map.put("Packets_Received_persec",MAX_DOUBLE);
        map.put("Packets_Sent_persec",MAX_DOUBLE);
        //win_swap rule params comparator
        map.put("Percent_Usage",MAX_DOUBLE);
        //win_system rule params comparator
        map.put("Context_Switches_persec",MAX_DOUBLE);
        map.put("Processor_Queue_Length",MAX_LONG);
        map.put("System_Calls_persec",MAX_DOUBLE);
        map.put("System_Up_Time",MAX_DOUBLE);
        //win_system_days rule params comparator
        map.put("count",MAX_LONG);
    }
    public static MonitorComparator getComparator(String key){
        return map.get(key);
    }
}
