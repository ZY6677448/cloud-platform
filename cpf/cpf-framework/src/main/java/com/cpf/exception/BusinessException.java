package com.cpf.exception;

import com.cpf.constants.ErrorDesc;

/**
 * @author jieping
 * @create 2018-04-25 19:56
 * @desc 业务异常
 **/
public class BusinessException  extends RuntimeException {
    private static final long serialVersionUID = 7684001206224294825L;
    private String errorCode;
    public BusinessException(ErrorDesc errorDesc){
        super(errorDesc.getErrorMSG());
        this.errorCode = errorDesc.getErrorCode();
    }
    public BusinessException(ErrorDesc errorDesc,Throwable cause){
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
