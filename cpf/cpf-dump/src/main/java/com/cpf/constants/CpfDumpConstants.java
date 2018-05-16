package com.cpf.constants;
/**
 * @author jieping
 * @create jieping on 2018-04-08
 * @desc dump层常量
 **/
public class CpfDumpConstants {

    public static final ErrorDesc SERIALIZATION_ERROR = new ErrorDesc("SERIALIZATION_ERROR","序列化模型异常");
    public static final ErrorDesc DESERIALIZATION_ERROR = new ErrorDesc("DESERIALIZATION_ERROR","反序列化模型异常");
    public static final ErrorDesc SET_OPTION_ERROR = new ErrorDesc("SET_OPTION_ERROR","模型参数设置异常");
    public static final ErrorDesc NOT_FOUND_MODEL = new ErrorDesc("NOT_FOUND_MODEL","找不到指定模型");
    public static final ErrorDesc QUERY_AVG_DATA_FAILED = new ErrorDesc("QUERY_AVG_DATA_FAILED","查询一定时间的平均监控数据失败");
    public static final ErrorDesc MONITOR_DATA_TYPE_ERROR = new ErrorDesc("MONITOR_DATA_TYPE_ERROR","监控数据类型错误");
    public static final ErrorDesc TRAIN_MODEL_ERROR = new ErrorDesc("TRAIN_MODEL_ERROR","模型训练异常");



}
