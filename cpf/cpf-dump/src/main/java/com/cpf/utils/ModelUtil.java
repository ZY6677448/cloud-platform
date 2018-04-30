package com.cpf.utils;

import com.alibaba.fastjson.JSON;
import com.cpf.constants.*;
import com.cpf.exception.BusinessException;
import com.cpf.exception.SystemException;
import com.cpf.influx.manager.DO.MonitorDO;
import com.cpf.logger.BusinessLogger;
import com.cpf.mysql.manager.DO.ModelDO;
import com.cpf.mysql.manager.DO.ModelOptionDO;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weka.classifiers.Classifier;
import weka.core.*;
import weka.core.converters.ArffSaver;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
/**
 * @author jieping
 * @create 2018-04-08
 **/
public class ModelUtil  {
    private static Logger logger = LoggerFactory.getLogger(ModelUtil.class);
    public static final String MODEL_PATH =  "cpf-dump/src/main/resources/";
    public static final String MODEL_SUFFIX = ".model";
    public static final String ARFF_SUFFIX = ".arff";
    /**
     * 整数的正则表达式
     */
    private static final Pattern PATTTERN = Pattern.compile("^[-\\+]?[\\d]*$");

    /**
     * 给定监控数据，训练指定的算法模型
     * @param modelDO 算法模型
     * @param monitorDOList 监控数据
     * @throws Exception
     */
    public static void train(ModelDO modelDO,List<MonitorDO> monitorDOList) throws Exception {

        if(!ValidationUtil.isNotNull(modelDO,monitorDOList) || !ValidationUtil.isNotNull( modelDO.getId())){
            throw new BusinessException(ErrorConstants.PARAMS_INVALID);
        }
        //读取模型
        Classifier classifier = (Classifier)deSerialization(modelDO.getId());
        OptionHandler optionHandler = (OptionHandler) classifier;
        //设置模型参数
        optionHandler.setOptions(getOptions(modelDO));
        try {
            //训练数据
            classifier.buildClassifier(ModelUtil.monitorDOS2Instances(monitorDOList));
            //训练后的模型持久化
            serialization(modelDO.getId(),classifier);
        } catch (Exception e) {
            throw new BusinessException(CpfDumpConstants.TRAIN_MODEL_ERROR);
        }
    }

    /**
     * 将训练模型持久化
     * @param id 模型对应的数据库信息ID
     * @param classifier 模型
     */
    private static void serialization(Long id, Object classifier){
        try {
            SerializationHelper.write(generateModelFileName(id),classifier);
        } catch (Exception e) {
            throw new SystemException(CpfDumpConstants.SERIALIZATION_ERROR,e);
        }
    }

    /**
     * 将算法模型持久化
     * @param modelDO
     */
    public static void serialization(@NotNull ModelDO modelDO){
        if(ValidationUtil.isNull(modelDO.getConfig())){
            BusinessLogger.errorLog("ModelUtil.serialization",
                    new String[]{JSON.toJSONString(modelDO)},
                    "MODEL_OPTION_NULL",
                    "模型参数为空",
                    logger);
            return;
        }
        Classifier classifier = ModelTypeEnum.getClasifier(modelDO.getConfig().getModelType());
        OptionHandler optionHandler = (OptionHandler) classifier;
        try {
            optionHandler.setOptions(getOptions(modelDO));
            serialization(modelDO.getId(),classifier);
        } catch (Exception e) {
            BusinessLogger.errorLog("ModelUtil.serialization",
                    new String[]{JSON.toJSONString(modelDO),JSON.toJSONString(e)},
                    "CLASSIFIER_SET_OPTION_ERROR",
                    "算法模型设置参数异常",
                    logger);
            return;        }
    }

    /**
     * 从文件提取出模型
     * @param id 模型对应的数据库信息ID
     * @return
     */
    public static Object deSerialization(Long id){
        Object classifier = null;
        try {
            classifier  =  SerializationHelper.read(generateModelFileName(id));
        } catch (Exception e) {
            throw new SystemException(CpfDumpConstants.DESERIALIZATION_ERROR,e);
        }
        return classifier;
    }
    /**
     * 设置模型参数，并持久化
     * @param modelDO
     */
    public static void setOptions(ModelDO modelDO){
        OptionHandler optionHandler = (OptionHandler) deSerialization(modelDO.getId());
        try {
            optionHandler.setOptions(getOptions(modelDO));
        } catch (Exception e) {
            throw new BusinessException(CpfDumpConstants.SET_OPTION_ERROR,e);
        }
        serialization(modelDO.getId(),optionHandler);
    }

    /**
     * 将domain对象的参数转换成weka分类器支持的参数
     * @param modelDO
     * @return
     */
    private static String[] getOptions(ModelDO modelDO){
        List<String> optionList = Lists.newArrayList();
        for(ModelOptionDO option : modelDO.getConfig().getOptions()){
            if(ValidationUtil.isNotNull(option.getValue())){
                if(option.getValueType() != OptionTypeEnum.BOOLEAN){
                    optionList.add(option.getKey());
                    optionList.add((String)option.getValue());
                }else {
                    if("true".equals(option.getValue().toString()) ){
                        optionList.add(option.getKey());
                    }
                }
            }
        }
        return optionList.toArray(new String[optionList.size()]);
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
            monitorDO.getData().put("danger",String.valueOf(random.nextBoolean()));
            for(Map.Entry<String,String> entry : monitorDO.getData().entrySet()) {
                List<String> list = monitorMap.get(entry.getKey());
                if(list == null){
                    list = Lists.newArrayList();
                    monitorMap.put(entry.getKey(),list);
                }
                list.add(entry.getValue());
            }
        }
        List<String> tagList = Lists.newArrayList(RuleTypeEnum.CPU.getTagList());
        ArrayList<Attribute> attributeArrayList = Lists.newArrayList();
        for(String key : monitorMap.keySet()){
            Attribute attribute = null;
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
        Instances instances = new Instances(monitorDOList.get(0).getType(),attributeArrayList,0);
        instances.setClassIndex(instances.numAttributes()-1);
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
    /**
     * 将监控数据转换为arff文件
     * @param monitorDOList
     * @return 文件路径+文件名称
     */
    public static String monitorDOS2Arff(List<MonitorDO> monitorDOList){
        Instances instances = monitorDOS2Instances(monitorDOList);
        ArffSaver saver = new ArffSaver();
        saver.setInstances(instances);
        String fileName = generateArffFileName(System.currentTimeMillis());
        try {
            saver.setFile(new File(fileName));
            saver.writeBatch();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileName;
    }

    /**
     * 删除本地算法模型
     * @param modelDO
     */
    public static void deleteModel(ModelDO modelDO){
        File file = new File(generateModelFileName(modelDO.getId()));
        if(file.exists()){
            if(file.delete()){
                BusinessLogger.infoLog("ModelUtil.deleteModel",
                        new String[]{JSON.toJSONString(modelDO)},
                        "success",
                        "删除模型成功",
                        logger);
            }else {
                BusinessLogger.errorLog("ModelUtil.deleteModel",
                        new String[]{JSON.toJSONString(modelDO)},
                        "DELETE_MODEL_FAILED",
                        "删除模型失败",
                        logger);
            }
        }
    }

    /**
     * 删除本地多个算法模型
     * @param modelDOList
     */
    public static void deleteModels(List<ModelDO> modelDOList){
        if(CollectionUtils.isNotEmpty(modelDOList)){
            modelDOList.forEach((modelDO -> deleteModel(modelDO)));
        }
    }

    /**
     * 根据模型id产生模型的全路径名
     * @param id
     * @return
     */
    private static String generateModelFileName(Long id){
        return MODEL_PATH + id + MODEL_SUFFIX;
    }
    /**
     * 根据模型id产生训练样本的全路径名
     * @param id
     * @return
     */
    private static String generateArffFileName(Long id){
        return MODEL_PATH + id + ARFF_SUFFIX;

    }
    /**
     * 判断value是否符合option的指定数据类型
     * @param value
     * @param option
     * @return
     */
    private  static boolean judgeOptionType(String value, ModelOptionDO option){
        if(option.getValueType() == OptionTypeEnum.INTEGER){
            return isInteger(value);
        }
        if(option.getValueType() == OptionTypeEnum.DOUBLE){
            return NumberUtils.isDigits(value);
        }
        if(option.getValueType() == OptionTypeEnum.ENUM){
            //TODO 需要修改为指定的枚举
            return true;
        }
        return false;
    }

    /**
     * 判断是否为整数
     * @param str
     * @return
     */
    private static boolean isInteger(String str) {
        return PATTTERN.matcher(str).matches();
    }

}
