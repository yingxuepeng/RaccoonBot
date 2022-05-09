package com.raccoon.qqbot.exception;

/**
 * Created by pyx on 6/25/15.
 */
public enum ServiceError {
    // common input
    COMMON_CUSTOM_MESSAGE(100000, "服务器错误"),
    COMMON_INPUT_INVALID(100001, "输入不合法"),
    COMMON_USER_MISSING(100002, "用户不是管理员"),

    // exec
    JSEXEC_ERROR(201001, "js脚本错误"),


    // external
    NETWORK_ERROR(302001, "网络错误"),
    QCLOUD_NLP_ERROR(302002, "腾讯云NLP请求错误"),

    // coding
    CODING_BRANCH_NOT_COVERAGE(990001, "未实现的逻辑分支");

    public int code;
    public String message;

    ServiceError(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
