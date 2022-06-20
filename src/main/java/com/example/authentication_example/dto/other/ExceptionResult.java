package com.example.authentication_example.dto.other;

public class ExceptionResult {
    private String timestamp;
    private String message;
    private String error;
    private Integer code;

    private String path;

    public ExceptionResult(String timestamp, String path, String message, String error, Integer code) {
        this.timestamp = timestamp;
        this.path = path;
        this.message = message;
        this.error = error;
        this.code = code;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
