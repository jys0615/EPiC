package com.semothon.team15.semo_backend.common.status;

public enum ErrorCode {
    BAD_REQUEST("400", "잘못된 요청"),
    UNAUTHORIZED("401", "인증되지 않은 사용자"),
    FORBIDDEN("403", "접근 권한 없음"),
    NOT_FOUND("404", "리소스를 찾을 수 없음"),
    INTERNAL_SERVER_ERROR("500", "서버 내부 오류");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() { return code; }
    public String getMessage() { return message; }
}
