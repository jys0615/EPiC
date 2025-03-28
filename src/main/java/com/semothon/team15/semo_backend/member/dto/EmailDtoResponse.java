package com.semothon.team15.semo_backend.member.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;

public class EmailDtoResponse<T> {
    @JsonIgnore
    private HttpServletRequest request;
    private HttpStatus status;
    private boolean success;
    private String message;
    private T data;

    public EmailDtoResponse() {
    }

    public EmailDtoResponse(HttpServletRequest request, HttpStatus status, boolean success, String message, T data) {
        this.request = request;
        this.status = status;
        this.success = success;
        this.message = message;
        this.data = data;
    }

    // Getters and Setters
    public HttpServletRequest getRequest() {
        return request;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}