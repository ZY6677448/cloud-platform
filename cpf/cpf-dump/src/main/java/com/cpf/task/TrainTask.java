package com.cpf.task;

import com.alibaba.fastjson.JSON;
import com.cpf.influx.manager.DO.MonitorDO;
import com.cpf.influx.manager.MonitorManager;
import com.cpf.logger.BusinessLogger;
import com.cpf.influx.holder.ModelHolder;
import com.cpf.mysql.manager.AggreModelManager;
import com.cpf.mysql.manager.DO.AggreModelDO;
import com.cpf.mysql.manager.DO.ModelDO;
import com.cpf.service.CallbackResult;
import com.cpf.service.ServiceExecuteTemplate;
import com.cpf.service.ServiceTemplate;
import com.cpf.utils.ModelUtil;
import com.cpf.utils.ValidationUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author jieping
 * @create 2018-04-25 19:56
 * @desc 定时训练任务
 **/
@Component
public class TrainTask extends ServiceTemplate  {
    private Logger logger = LoggerFactory.getLogger(TrainTask.class);
    /**
     * 定时训练间隔时间
     */
    private static Integer TRAIN_INTERVAL = 60*1000;
    /**
     * 错误样本占总训练样本的比例
     */
    private static final Double  DANGER_PROPORITION = 0.5D;

    @Autowired
    private AggreModelManager aggreModelManager;
    @Autowired
    private MonitorManager monitorManager;
    @Autowired
    private ModelHolder modelHolder;

    /**
     * 定时训练所有配置的算法，并保存算法模型
     * @return
     */
    //TODO 正式环境记得开启
    //@Scheduled(fixedRate = 1000)
   public void train(){
        Object result = execute(logger, "train", new ServiceExecuteTemplate() {
            @Override
            public CallbackResult<Object> checkParams() {
                return CallbackResult.success();
            }

            @Override
            public CallbackResult<Object> executeAction() throws Exception {
                List<AggreModelDO> aggreModelDOList = aggreModelManager.all().getResult();
                for(AggreModelDO aggreModelDO : aggreModelDOList){
                    List<MonitorDO> trainMOnitorDOList = getTrainSamples(aggreModelDO.getScene());
                    for(ModelDO modelDO : aggreModelDO.getModels()){
                        ModelUtil.train(modelDO,trainMOnitorDOList);
                    }
                }
                //算法模型更新
                modelHolder.refresh();
                return CallbackResult.success();
            }
        });
    }

    /**
     * 指定算法模型进行训练
     * @param modelDO
     */
    public void train(ModelDO modelDO){
       execute(logger, "train", new ServiceExecuteTemplate() {
           @Override
           public CallbackResult<Object> checkParams() {
               if(ValidationUtil.isNotNull(modelDO)){
                   return CallbackResult.success();
               }
               return CallbackResult.failure();
           }

           @Override
           public CallbackResult<Object> executeAction() throws Exception {
               AggreModelDO aggreModelDO = aggreModelManager.get(modelDO).getResult();
               List<MonitorDO> trainList = getTrainSamples(aggreModelDO.getScene());
               ModelUtil.train(modelDO,trainList);
               modelHolder.refresh(modelDO);
               return CallbackResult.success();
           }
       });


    }
    private List<MonitorDO> getTrainSamples(String scene){
        Map<String,String> tagMap = Maps.newHashMap();
        //查询有故障的监控数据
        tagMap.put("danger","true");
        List<MonitorDO> dangerMonitorDOList = monitorManager.queryDataByTime(scene,tagMap,null,null,null).getResult();
        if(CollectionUtils.isEmpty(dangerMonitorDOList)){
            BusinessLogger.errorLog("TrainTask.train",new String[]{JSON.toJSONString(scene)},"HAVE_NOT_ERROR_SAMPLE","没有错误样本可以训练",logger);
        }
        //查询正常的监控数据
        tagMap.put("danger","false");
        Long limit = Math.round(dangerMonitorDOList.size()/DANGER_PROPORITION);
        List<MonitorDO> safeMonitorDOList = monitorManager.queryDataByTime(scene,tagMap,null,null,null).getResult();
        List<MonitorDO> trainMOnitorDOList = Lists.newArrayList();
        trainMOnitorDOList.addAll(dangerMonitorDOList);
        trainMOnitorDOList.addAll(safeMonitorDOList);
        return trainMOnitorDOList;
    }
}
