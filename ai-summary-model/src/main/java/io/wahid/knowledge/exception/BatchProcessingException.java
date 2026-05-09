package io.wahid.knowledge.exception;

public class BatchProcessingException extends RuntimeException {

    public BatchProcessingException(String message) {
        super(message);
    }

    public BatchProcessingException(String message, Throwable cause) {
        super(message, cause);
    }

    public BatchProcessingException(Throwable cause) {
        super("Batch processing failed", cause);
    }
}
