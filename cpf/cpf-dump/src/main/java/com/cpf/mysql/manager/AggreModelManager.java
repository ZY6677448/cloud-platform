package com.cpf.mysql.manager;

import com.cpf.mysql.dao.AggreModelDAO;
import com.cpf.mysql.dao.PO.AggreModelPO;
import com.cpf.mysql.manager.DO.AggreModelDO;
import com.cpf.mysql.manager.DO.ModelDO;
import com.cpf.service.CallbackResult;
import com.cpf.service.ServiceExecuteTemplate;
import com.cpf.service.ServiceTemplate;
import com.cpf.utils.DOPOConverter;
import com.cpf.utils.ModelUtil;
import com.cpf.utils.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
/**
 * @author jieping
 * @create 2018-04-12
 * @desc 定时训练任务
 **/
@Component
public class AggreModelManager extends ServiceTemplate {
    @Autowired
    private AggreModelDAO aggreModelDAO;
    private static Logger logger = LoggerFactory.getLogger(AggreModelManager.class);

    /**
     * 保存聚合模型
     * @param aggreModelDO
     * @return
     */
    public CallbackResult<AggreModelDO> save(AggreModelDO aggreModelDO){
        Object  result = execute(logger, "save", new ServiceExecuteTemplate() {
            @Override
            public CallbackResult<Object> checkParams() {
                if(aggreModelDO == null){
                    return CallbackResult.failure();
                }
                return CallbackResult.success();
            }
            @Override
            public CallbackResult<Object> executeAction() {
                AggreModelPO result = aggreModelDAO.save(DOPOConverter.aggreModelDO2PO(aggreModelDO));
                return new CallbackResult<Object>(DOPOConverter.aggreModelPO2DO(result),true);
            }
        });
        return (CallbackResult<AggreModelDO>)result;
    }

    /**
     * 添加聚合模型的算法模型
     * @param aggreModelDO
     * @param modelDO
     * @return
     */
    public CallbackResult<ModelDO> addModel(AggreModelDO aggreModelDO,ModelDO modelDO){
        Object  result = execute(logger, "addModel", new ServiceExecuteTemplate() {
            @Override
            public CallbackResult<Object> checkParams() {
                if(aggreModelDO == null || modelDO == null || modelDO.getId() != null){
                    return CallbackResult.failure();
                }
                return CallbackResult.success();
            }
            @Override
            public CallbackResult<Object> executeAction() {
                aggreModelDO.getModels().add(modelDO);
                AggreModelDO saveResult = DOPOConverter.aggreModelPO2DO(aggreModelDAO.save(DOPOConverter.aggreModelDO2PO(aggreModelDO)));
                ModelDO returnResult =  saveResult.getModels().stream().filter(model -> model.getName().equals(modelDO.getName())).findFirst().orElse(null);
                ModelUtil.serialization(returnResult);
                return new CallbackResult<Object>(returnResult,true);
            }
        });
        return (CallbackResult<ModelDO>)result;
    }

    /**
     * 获取所有聚合模型
     * @return
     */
    public CallbackResult<List<AggreModelDO>> all(){
        Object  result = execute(logger, "all", new ServiceExecuteTemplate() {
            @Override
            public CallbackResult<Object> checkParams() {
                return CallbackResult.success();
            }
            @Override
            public CallbackResult<Object> executeAction() {
                List<AggreModelPO> result = aggreModelDAO.findAll();
                return new CallbackResult<Object>(DOPOConverter.aggreModelPOS2DOS(result),true);
            }
        });
        return (CallbackResult<List<AggreModelDO>>)result;
    }

    /**
     * 删除聚合模型
     * @param id 聚合模型 id
     * @return
     */
    public CallbackResult<Object> delete(Long id){
        Object  result = execute(logger, "delete", new ServiceExecuteTemplate() {
            @Override
            public CallbackResult<Object> checkParams() {
                if(id == null){
                    return CallbackResult.failure();
                }
                return CallbackResult.success();
            }
            @Override
            public CallbackResult<Object> executeAction() {
                aggreModelDAO.deleteById(id);
                return CallbackResult.success();
            }
        });
        return (CallbackResult<Object>)result;
    }

    /**
     * 获取聚合模型
     * @param id 聚合模型id
     * @return
     */
    public CallbackResult<AggreModelDO> get(Long id){
        Object  result = execute(logger, "get", new ServiceExecuteTemplate() {
            @Override
            public CallbackResult<Object> checkParams() {
                if(id == null){
                    return CallbackResult.failure();
                }
                return CallbackResult.success();
            }
            @Override
            public CallbackResult<Object> executeAction() {
                return new CallbackResult<Object>(DOPOConverter.aggreModelPO2DO(aggreModelDAO.getById(id)),true);
            }
        });
        return (CallbackResult<AggreModelDO>)result;
    }

    /**
     * 根据算法模型获取他对应的聚合模型
     * @param modelDO 聚合模型下的一个算法模型
     * @return
     */
    public CallbackResult<AggreModelDO> get(ModelDO modelDO){
        Object result = execute(logger, "get", new ServiceExecuteTemplate() {
            @Override
            public CallbackResult<Object> checkParams() {
                if(ValidationUtil.isNotNull(modelDO)){
                    return CallbackResult.success();
                }
                return CallbackResult.failure();
            }

            @Override
            public CallbackResult<Object> executeAction() throws Exception {
                return new CallbackResult<>(DOPOConverter.aggreModelPO2DO(aggreModelDAO.findByModelsIsContaining(DOPOConverter.modelDO2PO(modelDO))),true);
            }
        });
        return (CallbackResult<AggreModelDO>)result;
    }
}
