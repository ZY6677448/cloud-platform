package com.cpf.service;

import com.alibaba.fastjson.JSON;
import com.cpf.constants.ErrorDesc;
import lombok.Data;


/**
 * @author jieping
 * @create 2018-04-25 19:56
 * @desc 业务service执行模板返回结果
 * @param <T>
 **/

@Data
public class CallbackResult<T> {
    private Boolean success;
    private String errorCode;
    private T result;
    private String errorDesc;
    private String detail;
    private CallbackResult(Boolean success){
        this.success = success;
    }
    public CallbackResult(T result,Boolean success){
        this.result = result;
        this.success = success;
    }
    public CallbackResult(T result, Boolean success, ErrorDesc errorDesc){
        this.result = result;
        this.success = success;
        this.errorCode = errorDesc.getErrorCode();
        this.errorDesc = errorDesc.getErrorMSG();
    }
    public static CallbackResult<Object> failure(){
        return new CallbackResult<>(false);
    }
    public static CallbackResult<Object> failure(ErrorDesc errorDesc){
        CallbackResult<Object> callbackResult = CallbackResult.failure();
        callbackResult.setErrorCode(errorDesc.getErrorCode());
        callbackResult.setErrorDesc(errorDesc.getErrorMSG());
        return callbackResult;
    }
    public static CallbackResult<Object> failure(ErrorDesc errorDesc,Throwable cause){
        CallbackResult<Object> callbackResult = CallbackResult.failure(errorDesc);
        callbackResult.setDetail(JSON.toJSONString(cause));
        return callbackResult;
    }
    public static CallbackResult<Object> success(){
        return new CallbackResult<>(true);
    }
}
