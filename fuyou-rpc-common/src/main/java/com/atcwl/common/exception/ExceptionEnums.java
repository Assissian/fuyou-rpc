package com.atcwl.common.exception;

/**
 * 项目: simple-rpc
 * <p>
 * 功能描述: 异常枚举值
 *
 * @author: WuChengXing
 * @create: 2022-04-24 23:40
 **/
public enum ExceptionEnums {

    ;

    private final String code;
    private final String message;

    ExceptionEnums(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
