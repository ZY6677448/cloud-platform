package com.cpf.mysql.manager;

import com.cpf.constants.CpfDumpConstants;
import com.cpf.exception.BusinessException;
import com.cpf.holder.ModelHolder;
import com.cpf.mysql.dao.ModelDAO;
import com.cpf.mysql.dao.PO.ModelPO;
import com.cpf.mysql.manager.DO.ModelDO;
import com.cpf.service.CallbackResult;
import com.cpf.service.ServiceExecuteTemplate;
import com.cpf.service.ServiceTemplate;
import com.cpf.task.TrainTask;
import com.cpf.utils.DOPOConverter;
import com.cpf.utils.ModelUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/**
 * @author jieping
 * @create 2018-04-05
 *
 **/

@Component
public class ModelManager extends ServiceTemplate {
    @Autowired
    private ModelDAO modelDAO;
    @Autowired
    private TrainTask trainTask;
    @Autowired
    private ModelHolder modelHolder;
    private ExecutorService executorService = Executors.newFixedThreadPool(5);
    private static final Logger logger = LoggerFactory.getLogger(ModelManager.class);


    /**
     * 获取所有模型
     * @return
     */
    public CallbackResult<List<ModelDO>> all(){
        Object  result = execute(logger, "all", new ServiceExecuteTemplate() {
            @Override
            public CallbackResult<Object> checkParams() {
                return CallbackResult.success();
            }
            @Override
            public CallbackResult<Object> executeAction() {
                return new CallbackResult<>(DOPOConverter.modelPOs2DOs(modelDAO.findAll()), true);
            }
        });
        return (CallbackResult<List<ModelDO>>)result;
    }

    /**
     * 修改模型
     * @param modelDO 算法模型参数
     * @param train 是否需要训练模型
     * @return
     */
    public CallbackResult<ModelDO> modifyModel(ModelDO modelDO,Boolean train){
        Object  result = execute(logger, "modifyModel", new ServiceExecuteTemplate() {
            @Override
            public CallbackResult<Object> checkParams() {
                if(modelDO==null || modelDO.getConfig() == null){
                    return CallbackResult.failure();
                }
                if(modelDAO.getById(modelDO.getId()) == null){
                    throw new BusinessException(CpfDumpConstants.NOT_FOUND_MODEL);
                }
                return CallbackResult.success();
            }

            @Override
            public CallbackResult<Object> executeAction() {
                ModelPO modelPO = modelDAO.save(DOPOConverter.modelDO2PO(modelDO));
                ModelUtil.setOptions(modelDO);
                //异步进行模型训练
                if(train){
                    executorService.submit(()->trainTask.train(modelDO));
                }
                return new CallbackResult<>(DOPOConverter.modelPO2DO(modelPO), true);
            }
        });
        return (CallbackResult<ModelDO>)result;
    }

    /**
     * 删除模型
     * @param id 模型id
     * @return
     */
    public CallbackResult<Object> delete(Long id){
        Object  result = execute(logger, "delete", new ServiceExecuteTemplate() {
            @Override
            public CallbackResult<Object> checkParams() {
                if(id==null){
                    return CallbackResult.failure();
                }
                return CallbackResult.success();
            }
            @Override
            public CallbackResult<Object> executeAction() {
                ModelDO modelDO = DOPOConverter.modelPO2DO(modelDAO.getById(id));
                //删除内存中算法模型
                modelHolder.delete(modelDO);
                //删除硬盘中的算法模型
                ModelUtil.deleteModel(modelDO);
                //删除数据库中的算法模型
                modelDAO.deleteById(id);
                return CallbackResult.success();
            }
        });
        return (CallbackResult<Object>)result;
    }

}
