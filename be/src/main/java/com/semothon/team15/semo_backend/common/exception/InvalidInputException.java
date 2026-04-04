package com.semothon.team15.semo_backend.common.exception;

public class InvalidInputException extends RuntimeException {
    private final String fieldName;

    public InvalidInputException(String fieldName, String message) {
        super(message);
        this.fieldName = fieldName;
    }

    public InvalidInputException() {
        this("", "Invalid Input");
    }

    public String getFieldName() {
        return fieldName;
    }
}
