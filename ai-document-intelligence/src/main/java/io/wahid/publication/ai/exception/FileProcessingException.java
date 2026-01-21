package io.wahid.publication.ai.exception;

public class FileProcessingException extends RuntimeException {
    public FileProcessingException(String message, Throwable th) {
        super(message, th);
    }
}
