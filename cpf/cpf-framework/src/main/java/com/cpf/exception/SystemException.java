package com.cpf.exception;

import com.cpf.constants.ErrorDesc;
/**
 * @author jieping
 * @create 2018-04-25 19:56
 * @desc 系统异常
 **/
public class SystemException extends RuntimeException{
    private static final long serialVersionUID = 9180993166174148744L;
    private String errorCode;
    public SystemException(ErrorDesc errorDesc){
        super(errorDesc.getErrorMSG());
        this.errorCode = errorDesc.getErrorCode();
    }
    public SystemException(ErrorDesc errorDesc,Throwable cause){
        super(errorDesc.getErrorMSG(),cause);
        this.errorCode = errorDesc.getErrorCode();
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}
