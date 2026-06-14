package dev.szymon.gbenergymixbackend.exception;

public class InvalidHoursException extends RuntimeException {
    public InvalidHoursException(String message) {
        super(message);
    }
}
