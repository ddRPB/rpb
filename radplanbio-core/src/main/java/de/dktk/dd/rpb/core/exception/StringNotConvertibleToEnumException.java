package de.dktk.dd.rpb.core.exception;

public class StringNotConvertibleToEnumException extends Exception {

    public StringNotConvertibleToEnumException(String errorMessage) {
        super(errorMessage);
    }

    public StringNotConvertibleToEnumException(String errorMessage, Throwable throwable) {
        super(errorMessage, throwable);
    }
}
