package com.project.common.exception;

/**
 * Exception thrown when an external service fails
 */
public class ServiceUnavailableException extends BaseException {

    public ServiceUnavailableException(String serviceName) {
        super(
                String.format("Service '%s' is currently unavailable", serviceName),
                "SERVICE_UNAVAILABLE",
                503);
    }

    public ServiceUnavailableException(String serviceName, Throwable cause) {
        super(
                String.format("Service '%s' is currently unavailable", serviceName),
                "SERVICE_UNAVAILABLE",
                503,
                cause);
    }
}
