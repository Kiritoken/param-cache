package com.eli.param.cache.center.utils.result;

/**
 * @author eli
 */

public enum BizEnum {

    // 请求成功
    SUCCESS(200, "请求成功"),

    // 请求失败
    ERROR(500, "服务器异常");


    private final Integer code;

    private final String message;

    BizEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }


    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
