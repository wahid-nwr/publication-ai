package io.wahid.knowledge.exception;

public class FileProcessingException extends RuntimeException {
    public FileProcessingException(String message, Throwable th) {
        super(message, th);
    }

    public FileProcessingException(String message) {
        super(message);
    }
}
