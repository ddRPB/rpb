package de.dktk.dd.rpb.core.exception;

/**
 * Exception that represents the case if a mandatory property is missing and
 * that prevents to perform next steps in the workflow.
 */
public class MissingPropertyException extends Exception {
    public MissingPropertyException(String errorMessage) {
        super(errorMessage);
    }

    public MissingPropertyException(String errorMessage, Throwable throwable) {
        super(errorMessage, throwable);
    }
}