package com.cpf.mysql.manager;

import com.cpf.mysql.dao.AlarmDAO;
import com.cpf.mysql.dao.PO.AlarmPO;
import com.cpf.mysql.manager.DO.AlarmDO;
import com.cpf.service.CallbackResult;
import com.cpf.service.ServiceExecuteTemplate;
import com.cpf.service.ServiceTemplate;
import com.cpf.utils.DOPOConverter;
import com.cpf.utils.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author jieping
 * @create 2018-04-28 11:11
 * @desc 报警信息manager层
 **/
@Component
public class AlarmManager extends ServiceTemplate {
    @Autowired
    private AlarmDAO alarmDAO;
    private static final Logger logger = LoggerFactory.getLogger(AlarmManager.class);

    /**
     * 保存预警信息
     * @param alarmDO
     * @return
     */
    public CallbackResult<AlarmDO> save(AlarmDO alarmDO){
        Object result = execute(logger, "save", new ServiceExecuteTemplate() {
            @Override
            public CallbackResult<Object> checkParams() {
                if(ValidationUtil.isNotNull(alarmDO)){
                    return CallbackResult.success();
                }
                return CallbackResult.failure();
            }

            @Override
            public CallbackResult<Object> executeAction() {
                AlarmPO alarmPO = alarmDAO.save(DOPOConverter.alarmDO2PO(alarmDO));
                return new CallbackResult<>(DOPOConverter.alarmPO2DO(alarmPO),true);
            }
        });
        return (CallbackResult<AlarmDO>)result;
    }

    /**
     * 根据id获取报警信息
     * @param id
     * @return
     */
    public CallbackResult<AlarmDO> get(Long id){
        Object result = execute(logger, "get", new ServiceExecuteTemplate() {
            @Override
            public CallbackResult<Object> checkParams() {
                if(ValidationUtil.isNotNull(id)){
                    return CallbackResult.success();
                }
                return CallbackResult.failure();
            }

            @Override
            public CallbackResult<Object> executeAction() {
                AlarmPO alarmPO = alarmDAO.getById(id);
                return new CallbackResult<>(DOPOConverter.alarmPO2DO(alarmPO),true);
            }
        });
        return (CallbackResult<AlarmDO>)result;
    }

    /**
     * 根据是否过期获取报警信息
     * @param expire
     * @return
     */
    public CallbackResult<List<AlarmDO>> get(boolean expire){
        Object result = execute(logger, "get", new ServiceExecuteTemplate() {
            @Override
            public CallbackResult<Object> checkParams() {
                return CallbackResult.success();
            }

            @Override
            public CallbackResult<Object> executeAction() {
                List<AlarmPO> alarmPOList = alarmDAO.getByExpire(expire);
                return new CallbackResult<>(DOPOConverter.alarmPOS2DOS(alarmPOList),true);
            }
        });
        return (CallbackResult<List<AlarmDO>>)result;
    }


    /**
     * 获取所有预警信息
     * @return
     */
    public CallbackResult<List<AlarmDO>> all(){
        Object result = execute(logger, "all", new ServiceExecuteTemplate() {
            @Override
            public CallbackResult<Object> checkParams() {
                return CallbackResult.success();
            }

            @Override
            public CallbackResult<Object> executeAction() throws Exception {
                return new CallbackResult<>(DOPOConverter.alarmPOS2DOS(alarmDAO.findAll()),true);
            }
        });
        return (CallbackResult<List<AlarmDO>>)result;
    }
}
