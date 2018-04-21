package com.cpf.constants;

/**
 * Created by jieping on 2018-04-08
 */
public class CpfDumpConstants {
    public static ErrorDesc SERIALIZATION_ERROR = new ErrorDesc("SERIALIZATION_ERROR","序列化模型异常");
    public static ErrorDesc DESERIALIZATION_ERROR = new ErrorDesc("DESERIALIZATION_ERROR","反序列化模型异常");
    public static ErrorDesc SET_OPTION_ERROR = new ErrorDesc("SET_OPTION_ERROR","模型参数设置异常");
    public static ErrorDesc NOT_FOUND_MODEL = new ErrorDesc("NOT_FOUND_MODEL","找不到指定模型");
    public static ErrorDesc QUERY_AVG_DATA_FAILED = new ErrorDesc("QUERY_AVG_DATA_FAILED","查询一定时间的平均监控数据失败");
    public static ErrorDesc MONITOR_DATA_TYPE_ERROR = new ErrorDesc("MONITOR_DATA_TYPE_ERROR","监控数据类型错误");



}
