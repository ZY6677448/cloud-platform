package com.cpf.utils;

import org.apache.commons.lang3.math.NumberUtils;

import java.text.DecimalFormat;
/**
 * @author jieping
 * @create 2018-04-25 19:56
 * @desc java 和influx时间戳转换
 **/
public class TimeStampUtil {
    /**
     * java时间戳和influx时间戳相差倍数
     */
    private static final Long MULTIPLE = 1000000L;

    /**
     * java时间戳转influx时间戳
     * @param timestamp
     * @return
     */
    public static Long javaTime2Influx(Long timestamp){
        return timestamp * MULTIPLE;
    }

    /**
     * influx时间戳转java时间戳
     * @param timestamp
     * @return
     */
    public static Long influxTime2Java(Long timestamp){
        return  timestamp/MULTIPLE;
    }

    public static String influxTime2Java(String timestamp){
        return new DecimalFormat("0").format(NumberUtils.toDouble(timestamp));
    }

    /**
     * 分钟转毫秒
     * @param minutes
     * @return
     */
    public static Long minutes2Time(Long minutes){
        return minutes*60*1000;
    }
}
