package com.bookmyturf.exception;

public class CustomAppException extends RuntimeException {
    private final Object details;

    public CustomAppException(String message, Object details) {
        super(message);
        this.details = details;
    }

    public Object getDetails() {
        return details;
    }
}
