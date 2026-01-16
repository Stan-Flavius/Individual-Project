package com.parking;

public class SpotUnavailableException extends Exception {
    public SpotUnavailableException(String message) {
        super(message);
    }
}