package com.semothon.team15.semo_backend.common.dto;

import com.semothon.team15.semo_backend.common.status.ResultCode;

public class BaseResponse<T> {
    private final String resultCode;
    private final T data;
    private final String message;

    public BaseResponse() {
        this(ResultCode.SUCCESS.name(), null, ResultCode.SUCCESS.getMsg());
    }

    public BaseResponse(T data) {
        this(ResultCode.SUCCESS.name(), data, ResultCode.SUCCESS.getMsg());
    }

    public BaseResponse(String resultCode, T data, String message) {
        this.resultCode = resultCode;
        this.data = data;
        this.message = message;
    }

    // Getters
    public String getResultCode() { return resultCode; }
    public T getData() { return data; }
    public String getMessage() { return message; }
}
