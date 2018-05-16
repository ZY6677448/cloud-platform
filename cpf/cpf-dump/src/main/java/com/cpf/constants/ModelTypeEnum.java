package com.cpf.constants;

import com.alibaba.fastjson.JSON;
import com.cpf.logger.BusinessLogger;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.Logistic;
import weka.classifiers.functions.SMO;
import weka.classifiers.lazy.IBk;
import weka.classifiers.meta.AdaBoostM1;
import weka.classifiers.meta.Bagging;
import weka.classifiers.meta.LogitBoost;
import weka.classifiers.meta.Stacking;
import weka.classifiers.rules.JRip;
import weka.classifiers.rules.OneR;
import weka.classifiers.rules.PART;
import weka.classifiers.trees.DecisionStump;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.LMT;

import java.util.List;
import java.util.Map;
/**
 * @author jieping
 * @create 2018-04-09
 * @desc 算法模型类型
 **/
public enum ModelTypeEnum {
    IBk("k最近邻分类"),
    NAIVE_BAYES("朴素贝叶斯"),
    J48("C4.5决策树"),
    LMT("逻辑模型树"),
    DECISION_STUMP("单层决策树"),
    SMO("支持向量机"),
    ADABOOST_M1("AdaBoostM1"),
    Bagging("套袋法"),
    LOGIT_BOOST("LogitBoost"),
    STACKING("模型融合"),
    LOGISTIC("逻辑回归"),
    JRIP("规则学习"),
    ONE_R("1-R分类"),
    PART("PART决策");
    private String desc;
    private static final Logger logger = LoggerFactory.getLogger(ModelTypeEnum.class);
    ModelTypeEnum(String desc){
        this.desc = desc;
    }

    /**
     * 根据算法类型获取初始状态的算法模型
     * @param modelTypeEnum
     * @return
     */
    public static Classifier getClasifier(ModelTypeEnum modelTypeEnum){
        Classifier classifier = null;
        switch (modelTypeEnum){
            case IBk:classifier = new IBk();break;
            case NAIVE_BAYES:classifier = new NaiveBayes();break;
            case J48:classifier = new J48();break;
            case LMT:classifier = new LMT();break;
            case DECISION_STUMP:classifier = new DecisionStump();break;
            case SMO:classifier = new SMO();break;
            case ADABOOST_M1:classifier = new AdaBoostM1();break;
            case Bagging:classifier = new Bagging();break;
            case LOGIT_BOOST:classifier = new LogitBoost();break;
            case STACKING:classifier = new Stacking();break;
            case LOGISTIC:classifier = new Logistic();break;
            case JRIP:classifier = new JRip();break;
            case ONE_R:classifier = new OneR();break;
            case PART:classifier = new PART();break;
            default:BusinessLogger.errorLog("ModelTypeEnum.getClasifier",
                    new String[]{JSON.toJSONString(modelTypeEnum)},
                    "MODEL_TYPE_INVALID",
                    "模型类型非法",
                    logger);
        }
        return classifier;
    }
    /**
     * 获取 前端展示格式的规则类型
     * @return
     */
    public static List<Map<String,String>> getViewOptions(){
        List<Map<String,String>> list = Lists.newArrayList();
        for(ModelTypeEnum modelTypeEnum : ModelTypeEnum.values()){
            Map<String,String> map = Maps.newHashMap();
            map.put("value",modelTypeEnum.toString());
            map.put("label",modelTypeEnum.getDesc());
            list.add(map);
        }
        return list;
    }
    public String getDesc() {
        return desc;
    }

}
