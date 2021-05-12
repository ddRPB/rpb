package de.dktk.dd.rpb.core.exception;

/**
 * Exception that is thrown if an expected database item was not found.
 */
public class DataBaseItemNotFoundException extends Exception {

    public DataBaseItemNotFoundException(String errorMessage) {
        super(errorMessage);
    }

    public DataBaseItemNotFoundException(String errorMessage, Throwable throwable) {
        super(errorMessage, throwable);
    }
}
