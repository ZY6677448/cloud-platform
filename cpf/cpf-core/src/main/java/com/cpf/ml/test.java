package com.cpf.ml;

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
//        ConverterUtils.DataSource source = new ConverterUtils.DataSource(writeFile2);
//        Instances dataset = source.getDataSet();
//        //set class index to the last attribute
//        dataset.setClassIndex(dataset.numAttributes()-1);
//        //create and build the classifier!
//        IBk svm = new IBk();
//        svm.buildClassifier(dataset);
//        //System.out.println(svm.classifyInstance();
//
//        Evaluation evaluation = new Evaluation(dataset);
//        evaluation.crossValidateModel(svm,dataset,10,new Random(1));
//        System.out.println(evaluation.toSummaryString());
//        System.out.println(evaluation.pctCorrect());
//        SerializationHelper.write("cpf-core/src/main/resources/ibk.model",svm);
//        IBk classifier = (IBk)SerializationHelper.read("cpf-core/src/main/resources/ibk.model");
//        System.out.println(JSON.toJSONString(classifier.getOptions()));
        int v = 14;
        System.out.println(Integer.toBinaryString(v));

    }
}
