package com.cpf.influx.holder;

import com.alibaba.fastjson.JSON;
import com.cpf.logger.BusinessLogger;
import com.cpf.mysql.manager.AggreModelManager;
import com.cpf.mysql.manager.DO.AggreModelDO;
import com.cpf.mysql.manager.DO.ModelDO;
import com.cpf.mysql.manager.ModelManager;
import com.cpf.service.CallbackResult;
import com.cpf.utils.ModelUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import weka.classifiers.Classifier;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

/**
 * @author jieping
 * @create 2018-04-25 21:28
 * @desc 算法模型持有类，将持久化所有算法模型加载到内存中
 **/
@Component
public class ModelHolder {
    @Autowired
    private AggreModelManager aggreModelManager;
    @Autowired
    private ModelManager modelManager;
    private static Logger logger = LoggerFactory.getLogger(ModelHolder.class);

    private Map<String,List<CpfClassifier>> classifiesMap = Maps.newConcurrentMap();

    /**
     * 将持久化的算法模型读取到内存中
     */
    @PostConstruct
    public void refresh(){
        CallbackResult<List<AggreModelDO>> result = aggreModelManager.all();
        if(result.getSuccess()){
            for(AggreModelDO aggreModelDO : result.getResult()){
                refresh(aggreModelDO);
            }
            BusinessLogger.infoLog("ModelHolder.refresh",new String[]{JSON.toJSONString(result)},"success","算法模型刷新成功",logger);

        }else {
            BusinessLogger.errorLog("ModelHolder.refresh",new String[]{JSON.toJSONString(result)},"MODELS_REFRESH_FAILED","获取聚合模型失败",logger);

        }

    }

    /**
     * 读取指定场景的算法模型，替换掉现有的模型
     * @param aggreModelDO
     */
    public void refresh(AggreModelDO aggreModelDO){
        List<CpfClassifier> classifierList = Lists.newArrayList();
        for(ModelDO modelDO : aggreModelDO.getModels()){
            try {
                CpfClassifier cpfClassifier = new CpfClassifier();
                cpfClassifier.setWeight(modelDO.getWeight());
                cpfClassifier.setId(modelDO.getId());
                cpfClassifier.setClassifier((Classifier)ModelUtil.deSerialization(modelDO.getId()));
                classifierList.add(cpfClassifier);
            } catch (Exception e) {
                BusinessLogger.errorLog("ModelHolder.refresh",new String[]{JSON.toJSONString(modelDO),JSON.toJSONString(e)},"MODELS_DESERIALIZATION","算法模型反序列化失败",logger);
            }
        }
        classifiesMap.put(aggreModelDO.getScene(),classifierList);
    }
    public void refresh(ModelDO modelDO){
        Classifier classifier = (Classifier)ModelUtil.deSerialization(modelDO.getId());
        //内存查找，遍历所有分类器，查看是否有符合的(从数据库拿到聚合模型的场景费时，不采用)
        for(List<CpfClassifier> cpfClassifierList : classifiesMap.values()){
            CpfClassifier cpfClassifier = cpfClassifierList
                    .stream()
                    .filter((cpfClassifier1 -> cpfClassifier1.getId().equals(modelDO.getId())))
                    .findFirst()
                    .orElse(null);
            if(cpfClassifier != null){
                cpfClassifier.setClassifier(classifier);
                return;
            }
        }
        //内存查找不到，就是新增的分类器，获取模型对应的聚合模型，拿到聚合模型的使用场景
        AggreModelDO aggreModelDO = aggreModelManager.get(modelDO).getResult();
        List<CpfClassifier> cpfClassifierList = classifiesMap.get(aggreModelDO.getScene());
        CpfClassifier cpfClassifier = new CpfClassifier();
        cpfClassifier.setId(modelDO.getId());
        cpfClassifier.setClassifier(classifier);
        //内存中已经存在该聚合模型，则把分类器添加到该聚合模型下
        if(CollectionUtils.isNotEmpty(cpfClassifierList)){
            cpfClassifierList.add(cpfClassifier);
        }else {
            //内存中不存在该聚合模型，则新建一个聚合模型
            cpfClassifierList = Lists.newArrayList();
            cpfClassifierList.add(cpfClassifier);
            classifiesMap.put(aggreModelDO.getScene(),cpfClassifierList);
        }
    }

    /**
     * 删除内存中聚合模型对应的所有算法模型
     * @param aggreModelDO
     */
    public void delete(AggreModelDO aggreModelDO){
        classifiesMap.remove(aggreModelDO.getScene());
    }
    public void delete(ModelDO modelDO){
        //内存查找，遍历所有分类器，查看是否有符合的
        for(List<CpfClassifier> cpfClassifierList : classifiesMap.values()){
            CpfClassifier cpfClassifier = cpfClassifierList
                    .stream()
                    .filter((cpfClassifier1 -> cpfClassifier1.getId().equals(modelDO.getId())))
                    .findFirst()
                    .orElse(null);
            //移除该模型
            if(cpfClassifier != null){
                cpfClassifierList.remove(cpfClassifier);
                return;
            }
        }
    }
    public List<CpfClassifier> getClassifiers(String scene){
        if(classifiesMap.get(scene) == null){
            classifiesMap.put(scene,Lists.newArrayList());
        }
        return classifiesMap.get(scene);
    }
}
