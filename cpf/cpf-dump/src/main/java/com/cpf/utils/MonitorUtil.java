package com.cpf.utils;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.List;

/**
 * @author jieping
 * @create 2018-04-30 20:15
 * @desc 监控数据工具
 **/
public class MonitorUtil {
    private static List<String> byteColName = Lists.newArrayList();
    private static final Double M = 1024*1024D;
    static {
        byteColName.add("Available_Bytes");
    }

    /**
     * bytes转megaBytes
     * @param bytes
     * @return
     */
    public static String bytes2MB(String bytes){
        Double data = NumberUtils.toDouble(bytes);
        data/=M;
        return data.toString();

    }

    /**
     * 判断该监控数据是否是byte单位
     * @param colName
     * @return
     */
    public static boolean isBytes(String colName){
        return byteColName.contains(colName);
    }
}
