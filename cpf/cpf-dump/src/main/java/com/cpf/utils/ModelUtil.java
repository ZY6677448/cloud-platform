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
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.*;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.util.List;
import java.util.Random;

/**
 * @author jieping
 * @create 2018-04-08
 **/
@Component
public class ModelUtil  {
    private static final Logger logger = LoggerFactory.getLogger(ModelUtil.class);
    private static  String MODEL_PATH;
    private static final String MODEL_SUFFIX = ".model";
    private static final String ARFF_SUFFIX = ".arff";

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
        //将模型数据转换成instances格式
        Instances instances = MonitorUtil.monitorDOS2Instances(monitorDOList);
        //数据预处理
        instances = InstancesBuilder.construct(instances).standardize().replaceMissingValues().build();
        try {
            //训练数据
            classifier.buildClassifier(instances);
            //评估数据
            instances = InstancesBuilder.construct(instances).resample(10D).build();
            Evaluation evaluation = new Evaluation(instances);
            evaluation.crossValidateModel(classifier,instances,10,new Random(1));
            modelDO.setCorrectRate(evaluation.pctCorrect());
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
        }
    }

    /**
     * 从文件提取出模型
     * @param id 模型对应的数据库信息ID
     * @return
     */
    public static Object deSerialization(Long id){
        Object classifier;
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
            modelDOList.forEach((ModelUtil::deleteModel));
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


    @Value("${model.path}")
    public  void setModelPath(String modelPath) {
        MODEL_PATH = modelPath;
    }
}
