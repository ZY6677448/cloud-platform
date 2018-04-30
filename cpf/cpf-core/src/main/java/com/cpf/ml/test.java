package com.cpf.ml;

import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.meta.AdaBoostM1;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

import java.util.Random;

/**
 * Created by jieping on 2018-04-07
 */
public class test {
    public static void main(String[] args) throws Exception {
        double a = 6.4827975e+06;
        System.out.println(a);
        String readFile = "cpf-core/src/main/resources/abalone.txt";
        String writeFile = "cpf-core/src/main/resources/abalone.csv";
        String writeFile2 = "cpf-web/src/main/resources/data.arff";
        //FileConverter.txt2csv(readFile,writeFile);
        //FileConverter.csv2arff(writeFile,writeFile2);
        //load dataset
        ConverterUtils.DataSource source = new ConverterUtils.DataSource(writeFile2);
        Instances dataset = source.getDataSet();
        //set class index to the last attribute
        dataset.setClassIndex(dataset.numAttributes()-1);
        //create and build the classifier!
        AdaBoostM1 svm = new AdaBoostM1();
        svm.setOptions(null);
        svm.buildClassifier(dataset);
        double predict = svm.classifyInstance(dataset.get(1));
        System.out.println(dataset.classAttribute().value((int)predict));

        //System.out.println(svm.classifyInstance();

        Evaluation evaluation = new Evaluation(dataset);
        evaluation.crossValidateModel(svm,dataset,10,new Random(1));
        System.out.println(evaluation.toSummaryString());
//        SerializationHelper.write("cpf-core/src/main/resources/ibk.model",svm);
//        IBk classifier = (IBk)SerializationHelper.read("cpf-core/src/main/resources/ibk.model");
//        System.out.println(JSON.toJSONString(classifier.getOptions()));

    }
}
