package com.semothon.team15.semo_backend.common.status;

public enum UserSignUpResponseCode {
    SUCCESS(200, "Success"),
    MAIL_SEND_FAILED(500, "Mail send failed"),
    EXPIRED_AUTH_MAIL_CODE(400, "Expired authentication mail code"),
    INVALID_AUTH_MAIL_CODE(401, "Invalid authentication mail code");

    private final int code;
    private final String message;

    UserSignUpResponseCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() { return code; }
    public String getMessage() { return message; }
}