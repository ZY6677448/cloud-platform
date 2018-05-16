package com.cpf.utils;

import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Normalize;
import weka.filters.unsupervised.attribute.ReplaceMissingValues;
import weka.filters.unsupervised.attribute.Standardize;
import weka.filters.unsupervised.instance.Resample;

/**
 * @author jieping
 * @create 2018-05-03 21:56
 * @desc 训练数据处理工具
 **/
public class InstancesBuilder {
    private Instances instances;
    private InstancesBuilder(Instances instances){
        this.instances = instances;
    }
    public static InstancesBuilder construct(Instances instances){

        return new InstancesBuilder(instances);
    }

    /**
     * 归一化处理
     * @return
     * @throws Exception
     */
    public InstancesBuilder normalize() throws Exception {
        Normalize normalize = new Normalize();
        normalize.setInputFormat(instances);
        instances = Filter.useFilter(instances,normalize);
        return this;
    }

    /**
     * 标准化处理
     * @return
     * @throws Exception
     */
    public InstancesBuilder standardize() throws Exception {
        Standardize standardize = new Standardize();
        standardize.setInputFormat(instances);
        instances = Filter.useFilter(instances,standardize);
        return this;
    }

    /**
     * 替代缺失值
     * @return
     * @throws Exception
     */
    public InstancesBuilder replaceMissingValues() throws Exception {
        ReplaceMissingValues rmv = new ReplaceMissingValues();
        rmv.setInputFormat(instances);
        instances = Filter.useFilter(instances,rmv);
        return this;
    }
    public InstancesBuilder resample(Double percent) throws Exception {
        Resample resample = new Resample();
        resample.setOptions(new String[]{"-Z",percent.toString()});
        resample.setInputFormat(instances);
        instances = Filter.useFilter(instances,resample);
        return this;
    }
    public Instances build(){
        return instances;
    }

}
