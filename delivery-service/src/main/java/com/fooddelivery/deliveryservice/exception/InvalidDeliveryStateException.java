package com.fooddelivery.deliveryservice.exception;

public class InvalidDeliveryStateException extends RuntimeException {
    public InvalidDeliveryStateException(String message) {
        super(message);
    }

    public InvalidDeliveryStateException(String message, Throwable cause) {
        super(message, cause);
    }
}
