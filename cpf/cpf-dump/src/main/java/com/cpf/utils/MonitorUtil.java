package com.cpf.utils;

import com.cpf.constants.RuleTypeEnum;
import com.cpf.influx.manager.DO.MonitorDO;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author jieping
 * @create 2018-04-30 20:15
 * @desc 监控数据工具
 **/
public class MonitorUtil {
    private static final Logger logger = LoggerFactory.getLogger(MonitorUtil.class);
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

    /**
     * 监控数据转成weka接受的数据
     * @param monitorDOList
     * @return
     */
    public static Instances monitorDOS2Instances(List<MonitorDO> monitorDOList){
        if(CollectionUtils.isEmpty(monitorDOList)){
            return null;
        }
        Map<String,List<String>> monitorMap = Maps.newHashMap();
        Random random = new Random();
        //获取monitordo的所有值，map的key为属性名，
        for(MonitorDO monitorDO : monitorDOList){
            for(Map.Entry<String,String> entry : monitorDO.getData().entrySet()) {
                List<String> list = monitorMap.computeIfAbsent(entry.getKey(), k -> Lists.newArrayList());
                list.add(entry.getValue());
            }
        }
        List<String> tagList = Lists.newArrayList(RuleTypeEnum.getTagList(true));
        ArrayList<Attribute> attributeArrayList = Lists.newArrayList();
        for(String key : monitorMap.keySet()){
            Attribute attribute;
            //如果该属性为标签属性，则设置为nominal属性
            if(tagList.contains(key)){
                List<String> nominalList = monitorMap.get(key).stream().distinct().collect(Collectors.toList());
                attribute = new Attribute(key,nominalList);
            }else {
                attribute = new Attribute(key);
            }
            attributeArrayList.add(attribute);
        }
        Map<String,Attribute> attributeMap = attributeArrayList.stream().collect(Collectors.toMap(Attribute::name,Function.identity()));
        Instances instances = new Instances(monitorDOList.get(0).getType(),attributeArrayList,monitorDOList.size());
        instances.setClass(attributeArrayList.stream().filter(attribute -> "danger".equals(attribute.name())).findFirst().get());
        for(MonitorDO monitorDO : monitorDOList){
            Instance instance = new DenseInstance(attributeArrayList.size());
            for(Map.Entry<String,String> entry : monitorDO.getData().entrySet()){
                if(tagList.contains(entry.getKey())){
                    instance.setValue(attributeMap.get(entry.getKey()),entry.getValue());
                }else {
                    instance.setValue(attributeMap.get(entry.getKey()),new Double(entry.getValue()));

                }
            }
            instances.add(instance);
        }
        return instances;
    }

    /**
     * 单条监控数据转成weka的数据
     * @param monitorDO
     * @return
     */
    public static Instance monitorDO2Instance(MonitorDO monitorDO){
        if(monitorDO == null){
            return null;
        }
        Instances instances = monitorDOS2Instances(Lists.newArrayList(monitorDO));
        return instances.get(0);
    }
}
