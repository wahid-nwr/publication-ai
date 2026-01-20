package io.wahid.publication.ai.exception;

public class ErrorResponse {
    public String code;
    public String message;

    public ErrorResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
