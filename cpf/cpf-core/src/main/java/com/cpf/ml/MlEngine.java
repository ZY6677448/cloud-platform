package com.cpf.ml;

import com.cpf.constants.AlarmTypeEnum;
import com.cpf.constants.ErrorDesc;
import com.cpf.constants.TimeIntervalEnum;
import com.cpf.exception.BusinessException;
import com.cpf.influx.holder.CpfClassifier;
import com.cpf.influx.holder.ModelHolder;
import com.cpf.influx.manager.DO.MonitorDO;
import com.cpf.influx.manager.MonitorManager;
import com.cpf.mysql.manager.AlarmManager;
import com.cpf.mysql.manager.AssetManager;
import com.cpf.mysql.manager.DO.AlarmDO;
import com.cpf.mysql.manager.DO.AssetDO;
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
    @Autowired
    private AssetManager assetManager;
    @Autowired
    private MonitorManager monitorManager;
    private static final String unit = TimeIntervalEnum.generateInterval(TimeIntervalEnum.HOUR,1L);
    private static Logger logger = LoggerFactory.getLogger(MlEngine.class);


    public void predict(){
        execute(logger, "predict", new ServiceExecuteTemplate() {
            @Override
            public CallbackResult<Object> checkParams() {
                return null;
            }

            @Override
            public CallbackResult<Object> executeAction() throws Exception {
                List<AssetDO> assetDOList = assetManager.all().getResult();
                //遍历所有资产
                for (AssetDO assetDO : assetDOList) {
                    //查询资产的所有最新系统监控数据
                    CallbackResult<List<MonitorDO>> recentAllData = monitorManager.queryRecentAllData(assetDO.getHostname());
                    if (recentAllData.getSuccess()) {
                        for (MonitorDO monitorDO : recentAllData.getResult()) {
                            //查询监控数据变化率
                            CallbackResult<MonitorDO> changeRateData = monitorManager.queryChangeRateByTime(monitorDO, unit);
                            if (changeRateData.getSuccess()) {
                                //预测系统是否出现问题
                                predict(changeRateData.getResult());
                            } else {
                                throw new BusinessException(new ErrorDesc("QUERY_CHANGE_RATE_DATA_FAILED", "查询数据变化率失败"));
                            }
                        }
                    } else {
                        throw new BusinessException(new ErrorDesc("QUERY_RECENT_ALL_DATA_FAILED", "查询最新数据失败"));
                    }
                }
                return CallbackResult.success();
            }
        });
    }
    /**
     * 根据监控数据预测是否存在危险
     * @param monitorDO 监控数据
     */
    public void predict(MonitorDO monitorDO) throws Exception {
        if(ValidationUtil.isNull(monitorDO)){
            return;
        }
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
    }
}
