package com.cpf.service;

import com.alibaba.fastjson.JSON;
import com.cpf.constants.ErrorConstants;
import com.cpf.constants.ErrorDesc;
import com.cpf.exception.BusinessException;
import com.cpf.exception.SystemException;
import com.cpf.logger.BusinessLogger;
import org.slf4j.Logger;


/**
 * @author jieping
 * @create 2018-04-25 19:56
 * @desc 业务service执行模板
 **/
public class ServiceTemplate {
    public CallbackResult<Object> execute( Logger logger,String methodName,ServiceExecuteTemplate executeTemplate){
        methodName = this.getClass().toString()+ "."+methodName;
        Long time = System.currentTimeMillis();
        CallbackResult<Object> checkResult;
        CallbackResult<Object> executeResult;
        //参数校验
        try {
            checkResult = executeTemplate.checkParams();
        }catch (BusinessException e) {
            BusinessLogger.errorLog(methodName,new String[]{JSON.toJSONString(e)},e.getErrorCode(),e.getMessage(),logger);
            return CallbackResult.failure(new ErrorDesc(e.getErrorCode(),e.getMessage()),e);
        }
        catch (SystemException e) {
            BusinessLogger.errorLog(methodName,new String[]{JSON.toJSONString(e)},e.getErrorCode(),e.getMessage(),logger);
            return CallbackResult.failure(new ErrorDesc(e.getErrorCode(),e.getMessage()),e);
        }
        catch (Exception e) {
            BusinessLogger.errorLog(methodName,new String[]{JSON.toJSONString(e)},ErrorConstants.SYSTEM_RRROR.getErrorCode(),ErrorConstants.SYSTEM_RRROR.getErrorMSG(),logger);
            return CallbackResult.failure(ErrorConstants.SYSTEM_RRROR,e);
        }
        //参数校验结果判断
        if(!checkResult.getSuccess()){
            return CallbackResult.failure(ErrorConstants.PARAMS_INVALID);
        }
        //执行主体代码
        try {
            executeResult = executeTemplate.executeAction();
        }catch (BusinessException e) {
            BusinessLogger.errorLog(methodName,new String[]{JSON.toJSONString(e)},e.getErrorCode(),e.getMessage(),logger);
            return CallbackResult.failure(new ErrorDesc(e.getErrorCode(),e.getMessage()),e);
        }
        catch (SystemException e) {
            BusinessLogger.errorLog(methodName,new String[]{JSON.toJSONString(e)},e.getErrorCode(),e.getMessage(),logger);
            return CallbackResult.failure(new ErrorDesc(e.getErrorCode(),e.getMessage()),e);
        }
        catch (Exception e) {
            BusinessLogger.errorLog(methodName,null,ErrorConstants.SYSTEM_RRROR.getErrorCode(),ErrorConstants.SYSTEM_RRROR.getErrorMSG(),logger);
            return CallbackResult.failure(ErrorConstants.SYSTEM_RRROR,e);
        }
        String executeTime = String.valueOf(System.currentTimeMillis() -time)+"ms";
        BusinessLogger.infoLog(methodName,null,"executeTime",executeTime,logger);
        return executeResult;
    }
}
