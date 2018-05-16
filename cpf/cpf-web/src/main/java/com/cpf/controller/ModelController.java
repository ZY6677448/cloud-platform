package com.cpf.controller;

import com.cpf.constants.ModelTypeEnum;
import com.cpf.constants.RuleTypeEnum;
import com.cpf.mysql.manager.AggreModelManager;
import com.cpf.mysql.manager.DO.AggreModelDO;
import com.cpf.mysql.manager.DO.ModelDO;
import com.cpf.mysql.manager.ModelManager;
import com.cpf.service.CallbackResult;
import com.cpf.utils.ModelFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


/**
 * @author jieping
 * @create 2018-04-05
 **/
@RestController
@RequestMapping("/config")
public class ModelController {
    @Autowired
    private ModelManager modelManager;
    @Autowired
    private AggreModelManager aggreModelManager;
    /**
     * 获取模型的类型
     * @return
     */
    @RequestMapping(value = "/modelType", method = RequestMethod.GET)
    ResponseEntity<Object> modelType(){
        return new ResponseEntity<>(new CallbackResult<>(ModelTypeEnum.getViewOptions(), true), HttpStatus.OK);
    }
    /**
     * 获取应用场景的类型
     * @return
     */
    @RequestMapping(value = "/scenes", method = RequestMethod.GET)
    ResponseEntity<Object> scenes(){
        return new ResponseEntity<>(new CallbackResult<>(RuleTypeEnum.getViewOptions(), true), HttpStatus.OK);
    }
    /**
     * 获取模型类
     * @param name 模型名称
     * @param modelType 模型类型
     * @return
     */
    @RequestMapping(value = "/model",method = RequestMethod.GET)
    ResponseEntity<Object> model(String name, String modelType,Long aggreModelId){
        CallbackResult<AggreModelDO> result = aggreModelManager.get(aggreModelId);
        if(result.getSuccess()){
            ModelDO modelDO = ModelFactory.getModel(name,modelType);
            return new ResponseEntity<>(aggreModelManager.addModel(result.getResult(), modelDO), HttpStatus.OK);

        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * 获取所有的模型
     * @return
     */
    @RequestMapping(value = "/models",method = RequestMethod.GET)
    ResponseEntity<Object> models(){
        return new ResponseEntity<>(modelManager.all(), HttpStatus.OK);
    }
    /**
     * 保存模型
     * @param modelDO
     * @return
     */
    @RequestMapping(value = "/model",method = RequestMethod.POST)
    ResponseEntity<Object> model(@RequestBody ModelDO modelDO){
        CallbackResult<ModelDO> result = modelManager.modifyModel(modelDO,true);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * 删除指定模型
     * @param id 模型id
     * @return
     */
    @RequestMapping(value = "/model",method = RequestMethod.DELETE)
    ResponseEntity<Object> model(@RequestParam Long id){
        CallbackResult<Object> result = modelManager.delete(id);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * 获取所有聚合模型
     * @return
     */
    @RequestMapping(value = "/aggremodels",method = RequestMethod.GET)
    ResponseEntity<Object> aggremodels(){
        return new ResponseEntity<>(aggreModelManager.all(), HttpStatus.OK);
    }

    /**
     * 新增和保存聚合模型
     * @param aggreModelDO
     * @return
     */
    @RequestMapping(value = "/aggremodel",method = RequestMethod.POST)
    ResponseEntity<Object> aggremodel(@RequestBody AggreModelDO aggreModelDO){
        CallbackResult<AggreModelDO> result = aggreModelManager.save(aggreModelDO);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
    @RequestMapping(value = "/aggremodel",method = RequestMethod.GET)
    ResponseEntity<Object> getAggreModel( Long id){
        return new ResponseEntity<>(aggreModelManager.get(id), HttpStatus.OK);
    }

    /**
     * 删除指定模型
     * @param id 模型id
     * @return
     */
    @RequestMapping(value = "/aggremodel",method = RequestMethod.DELETE)
    ResponseEntity<Object> aggremodel(@RequestParam Long id){
        return new ResponseEntity<>(aggreModelManager.delete(id), HttpStatus.OK);
    }
}
