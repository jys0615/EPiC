package com.semothon.team15.semo_backend.common.exception;

import com.semothon.team15.semo_backend.common.dto.BaseResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<BaseResponse<Object>> handleCustomException(CustomException e) {
        return ResponseEntity
                .status(e.getErrorCode().getStatus())
                .body(new BaseResponse<>("FAIL", null, e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<Object>> handleGenericException(Exception e) {
        return ResponseEntity
                .internalServerError()
                .body(new BaseResponse<>("ERROR", null, "서버 오류: " + e.getMessage()));
    }
}

/*
 * MemberService → throw new CustomException(ErrorCode.DUPLICATE_LOGIN_ID,
 * "이미 등록된 아이디")
 * ↓
 * GlobalExceptionHandler.handleCustomException() 가 잡음
 * ↓
 * ErrorCode.DUPLICATE_LOGIN_ID.getStatus() → HttpStatus.CONFLICT (409)
 * ↓
 * 클라이언트에 409 응답 반환
 */