package com.cpf.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.cpf.constants.AlarmTypeEnum;
import com.cpf.constants.ModelTypeEnum;
import com.cpf.influx.manager.DO.MonitorDO;
import com.cpf.mysql.dao.PO.*;
import com.cpf.mysql.manager.DO.*;
import com.cpf.utils.mapper.AssetMapper;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.Map;
/**
 * @author jieping
 * @create 2018-04-09
 * @desc DO PO 转换工具
 **/
public class DOPOConverter {
    public static ModelPO modelDO2PO(ModelDO modelDO){
        ModelPO modelPO = new ModelPO();
        modelPO.setId(modelDO.getId());
        modelPO.setName(modelDO.getName());
        modelPO.setWeight(modelDO.getWeight());
        modelPO.setConfig(modelOptionsDO2PO(modelDO.getConfig()));
        modelPO.setCorrectRate(modelDO.getCorrectRate());
        return modelPO;
    }
    public static ModelDO modelPO2DO(ModelPO modelPO){
        ModelDO modelDO = new ModelDO();
        modelDO.setId(modelPO.getId());
        modelDO.setName(modelPO.getName());
        modelDO.setWeight(modelPO.getWeight());
        modelDO.setConfig(modelOptionsPO2DO(modelPO.getConfig()));
        modelDO.setCorrectRate(modelPO.getCorrectRate());
        return modelDO;
    }
    public static List<ModelDO> modelPOs2DOs(List<ModelPO> modelPOList){
        List<ModelDO> list = Lists.newArrayList();
        if(CollectionUtils.isNotEmpty(modelPOList)){
            for(ModelPO modelPO : modelPOList){
                list.add(modelPO2DO(modelPO));
            }
        }
        return list;
    }
    public static List<ModelPO> modelDOs2POs(List<ModelDO> modelDOList){
        List<ModelPO> list = Lists.newArrayList();
        if(CollectionUtils.isNotEmpty(modelDOList)){
            for(ModelDO modelDO : modelDOList){
                list.add(modelDO2PO(modelDO));
            }
        }
        return list;
    }

    public static ModelOptionsDO modelOptionsPO2DO(ModelOptionsPO modelOptionsPO){
        ModelOptionsDO modelOptionsDO = new ModelOptionsDO();
        modelOptionsDO.setId(modelOptionsPO.getId());
        modelOptionsDO.setModelType(ModelTypeEnum.valueOf(modelOptionsPO.getModelType()));
        modelOptionsDO.setOptions(JSON.parseObject(modelOptionsPO.getOptions(),new TypeReference<List<ModelOptionDO>>(){}));
        return modelOptionsDO;
    }
    public static ModelOptionsPO modelOptionsDO2PO(ModelOptionsDO modelOptionsDO){
        ModelOptionsPO modelOptionsPO = new ModelOptionsPO();
        modelOptionsPO.setId(modelOptionsDO.getId());
        modelOptionsPO.setModelType(modelOptionsDO.getModelType().toString());
        modelOptionsPO.setOptions(JSON.toJSONString(modelOptionsDO.getOptions()));
        return modelOptionsPO;
    }
    public static AggreModelDO aggreModelPO2DO(AggreModelPO aggreModelPO){
        AggreModelDO aggreModelDO = new AggreModelDO();
        aggreModelDO.setId(aggreModelPO.getId());
        aggreModelDO.setScene(aggreModelPO.getScene());
        aggreModelDO.setModels(modelPOs2DOs(aggreModelPO.getModels()));
        return aggreModelDO;
    }
    public static AggreModelPO aggreModelDO2PO(AggreModelDO aggreModelDO){
        AggreModelPO aggreModelPO = new AggreModelPO();
        aggreModelPO.setId(aggreModelDO.getId());
        aggreModelPO.setScene(aggreModelDO.getScene());
        aggreModelPO.setModels(modelDOs2POs(aggreModelDO.getModels()));
        return aggreModelPO;
    }
    public static List<AggreModelDO> aggreModelPOS2DOS(List<AggreModelPO> aggreModelPOList){
        List<AggreModelDO> list = Lists.newArrayList();
        if(CollectionUtils.isNotEmpty(aggreModelPOList)){
            for(AggreModelPO aggreModelPO : aggreModelPOList){
                list.add(aggreModelPO2DO(aggreModelPO));
            }
        }
        return list;
    }
    public static RuleDO rulePO2DO(RulePO rulePO){
        RuleDO ruleDO = new RuleDO();
        ruleDO.setId(rulePO.getId());
        ruleDO.setName(rulePO.getName());
        ruleDO.setType(rulePO.getType());
        ruleDO.setCreateTime(rulePO.getCreateTime());
        ruleDO.setModifyTime(rulePO.getModifyTime());
        ruleDO.setTime(rulePO.getTime());
        ruleDO.setConfig(JSON.parseObject(rulePO.getConfig(),new TypeReference<Map<String,String>>(){}));
        return ruleDO;
    }
    public static RulePO ruleDO2PO(RuleDO ruleDO){
        RulePO rulePO = new RulePO();
        rulePO.setId(ruleDO.getId());
        rulePO.setName(ruleDO.getName());
        rulePO.setType(ruleDO.getType());
        rulePO.setCreateTime(ruleDO.getCreateTime());
        rulePO.setModifyTime(ruleDO.getModifyTime());
        rulePO.setConfig(JSON.toJSONString(ruleDO.getConfig()));
        rulePO.setTime(ruleDO.getTime());
        return rulePO;
    }
    public static List<RuleDO> rulePOS2DOS(List<RulePO> rulePOList){
        List<RuleDO> list = Lists.newArrayList();
        if(CollectionUtils.isNotEmpty(rulePOList)){
            for(RulePO rulePO : rulePOList){
                list.add(rulePO2DO(rulePO));
            }
        }
        return list;
    }
    public static List<AssetDO> assetPOS2DOS(List<AssetPO> assetPOList){
        return AssetMapper.INSTANCE.assetPOS2DOS(assetPOList);
    }
    public static AlarmPO alarmDO2PO(AlarmDO alarmDO){
        AlarmPO alarmPO = new AlarmPO();
        alarmPO.setId(alarmDO.getId());
        alarmPO.setType(alarmDO.getType().toString());
        alarmPO.setData(JSON.toJSONString(alarmDO.getMonitorDO()));
        alarmPO.setRule(JSON.toJSONString(alarmDO.getRuleDO()));
        alarmPO.setTime(alarmDO.getTime());
        alarmPO.setExpire(alarmDO.getExpire());
        return alarmPO;
    }
    public static AlarmDO alarmPO2DO(AlarmPO alarmPO){
        AlarmDO alarmDO = new AlarmDO();
        alarmDO.setId(alarmPO.getId());
        alarmDO.setType(AlarmTypeEnum.valueOf(alarmPO.getType()));
        alarmDO.setTime(alarmPO.getTime());
        alarmDO.setMonitorDO(JSON.parseObject(alarmPO.getData(),new TypeReference<MonitorDO>(){}));
        alarmDO.setRuleDO(JSON.parseObject(alarmPO.getRule(),new TypeReference<RuleDO>(){}));
        alarmDO.setExpire(alarmPO.getExpire());
        return alarmDO;
    }
    public static List<AlarmDO> alarmPOS2DOS(List<AlarmPO> alarmPOList){
        List<AlarmDO> list = Lists.newArrayList();
        if(CollectionUtils.isNotEmpty(alarmPOList)){
            for(AlarmPO alarmPO : alarmPOList){
                list.add(alarmPO2DO(alarmPO));
            }
        }
        return list;
    }
}
