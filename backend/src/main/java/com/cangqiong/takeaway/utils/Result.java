package com.cangqiong.takeaway.utils;

import lombok.Data;

@Data
public class Result<T> {

    private Integer code;
    private String msg;
    private T data;

    public static final int SUCCESS_CODE = 200;
    public static final int UNAUTHORIZED_CODE = 401;
    public static final int FORBIDDEN_CODE = 403;
    public static final int NOT_FOUND_CODE = 404;
    public static final int ERROR_CODE = 500;

    public static final String SUCCESS_MSG = "success";
    public static final String ERROR_MSG = "error";
    public static final String UNAUTHORIZED_MSG = "未授权，请先登录";
    public static final String FORBIDDEN_MSG = "无权限访问";
    public static final String NOT_FOUND_MSG = "资源不存在";

    public static <T> Result<T> success() {
        return success(null);
    }

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(SUCCESS_CODE);
        result.setMsg(SUCCESS_MSG);
        result.setData(data);
        return result;
    }

    public static <T> Result<T> error(String msg) {
        return error(ERROR_CODE, msg);
    }

    public static <T> Result<T> error(int code, String msg) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }

    public static <T> Result<T> unauthorized() {
        return error(UNAUTHORIZED_CODE, UNAUTHORIZED_MSG);
    }

    public static <T> Result<T> unauthorized(String msg) {
        return error(UNAUTHORIZED_CODE, msg);
    }

    public static <T> Result<T> forbidden() {
        return error(FORBIDDEN_CODE, FORBIDDEN_MSG);
    }

    public static <T> Result<T> notFound() {
        return error(NOT_FOUND_CODE, NOT_FOUND_MSG);
    }
}
