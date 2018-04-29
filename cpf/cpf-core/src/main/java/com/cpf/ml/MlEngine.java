package com.cpf.ml;

import com.cpf.constants.AlarmTypeEnum;
import com.cpf.influx.holder.CpfClassifier;
import com.cpf.influx.holder.ModelHolder;
import com.cpf.influx.manager.DO.MonitorDO;
import com.cpf.mysql.manager.AlarmManager;
import com.cpf.mysql.manager.DO.AlarmDO;
import com.cpf.service.CallbackResult;
import com.cpf.service.ServiceExecuteTemplate;
import com.cpf.service.ServiceTemplate;
import com.cpf.utils.ModelUtil;
import com.cpf.utils.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import weka.core.Instance;

import java.util.Date;
import java.util.List;

/**
 * @author jieping
 * @create 2018-04-25 19:44
 * @desc 机器学习算法引擎
 **/
@Component
public class MlEngine extends ServiceTemplate {
    @Autowired
    private ModelHolder modelHolder;
    private static final Double THRESHOLD  = 0.5D;
    @Autowired
    private AlarmManager alarmManager;
    private static Logger logger = LoggerFactory.getLogger(MlEngine.class);

    /**
     * 根据监控数据预测是否存在危险
     * @param monitorDO 监控数据
     */
    public void predict(MonitorDO monitorDO){
        execute(logger, "predict", new ServiceExecuteTemplate() {
            @Override
            public CallbackResult<Object> checkParams() {
                if(ValidationUtil.isNotNull(monitorDO)){
                    return CallbackResult.success();
                }
                return CallbackResult.failure();
            }

            @Override
            public CallbackResult<Object> executeAction() throws Exception {
                List<CpfClassifier> cpfClassifierList = modelHolder.getClassifiers(monitorDO.getType());
                Instance instance = ModelUtil.monitorDO2Instance(monitorDO);
                //计算权重
                Double sum = 0D;
                Double predicts = 0D;
                for(CpfClassifier cpfClassifier : cpfClassifierList){
                    sum+=cpfClassifier.getWeight();
                    Double predict = cpfClassifier.getClassifier().classifyInstance(instance)*cpfClassifier.getWeight();
                    predicts+=predict;
                }
                predicts /= sum;
                //如果小于阈值，代表存在危险
                if(predicts < THRESHOLD){
                    //保存预警信息
                    AlarmDO alarmDO = new AlarmDO();
                    alarmDO.setType(AlarmTypeEnum.PREDICT);
                    alarmDO.setMonitorDO(monitorDO);
                    alarmDO.setTime(new Date());
                    alarmManager.save(alarmDO);
                }
                return CallbackResult.success();
            }
        });

    }
}
