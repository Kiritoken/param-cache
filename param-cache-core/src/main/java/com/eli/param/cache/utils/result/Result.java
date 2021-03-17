package com.eli.param.cache.utils.result;


import lombok.Data;

import java.io.Serializable;

/**
 * @author eli
 */
@Data
public class Result<T> extends BaseResult implements Serializable {


    /**
     * 成功返回的数据
     */
    private T result;


    public static <D> Result<D> wrapSuccessfulResult(D data) {
        Result<D> result = new Result<>();
        result.result = data;
        result.success = true;
        result.code = BizEnum.SUCCESS.getCode();
        return result;
    }

    public static <D> Result<D> wrapSuccessfulResult(D data, String message) {
        Result<D> result = new Result<D>();
        result.result = data;
        result.success = true;
        result.code = BizEnum.SUCCESS.getCode();
        result.setMessage(message);
        return result;
    }


    public static <D> Result<D> wrapErrorResult(BizEnum error) {
        Result<D> result = new Result<D>();
        result.success = false;
        result.code = error.getCode();
        result.message = error.getMessage();
        return result;
    }


    public static <D> Result<D> wrapErrorResult(Integer code, String message) {
        Result<D> result = new Result<D>();
        result.success = false;
        result.code = code;
        result.message = message;
        return result;
    }


    public static <D> Result<D> wrapErrorResult(D data, Integer code, String message) {
        Result<D> result = new Result<D>();
        result.result = data;
        result.success = false;
        result.code = code;
        result.message = message;
        return result;
    }


    public T getResult() {
        return result;
    }

}
