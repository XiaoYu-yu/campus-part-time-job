package com.cangqiong.takeaway.exception;

public class BusinessException extends RuntimeException {

    private Integer code;

    public BusinessException(String msg) {
        super(msg);
        this.code = 500;
    }

    public BusinessException(Integer code, String msg) {
        super(msg);
        this.code = code;
    }

    public BusinessException(String msg, Throwable cause) {
        super(msg, cause);
        this.code = 500;
    }

    public Integer getCode() {
        return code;
    }
}
