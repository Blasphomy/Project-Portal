package com.project.common.exception;

/**
 * Exception thrown when validation fails
 */
public class ValidationException extends BaseException {

    public ValidationException(String message) {
        super(message, "VALIDATION_ERROR", 400);
    }

    public ValidationException(String field, String message) {
        super(
                String.format("Validation failed for field '%s': %s", field, message),
                "VALIDATION_ERROR",
                400);
    }
}
