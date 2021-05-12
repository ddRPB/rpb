package de.dktk.dd.rpb.core.exception;

public class WrongDateFormatException extends Exception {
    /**
     * Constructs a new exception with the specified detail message.  The
     * cause is not initialized, and may subsequently be initialized by
     * a call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public WrongDateFormatException(String message) {
        super(message);
    }

    public WrongDateFormatException(String providedString, String expectedDateFormat) {
        super("The provided string has the wrong format to be parsed as date. Supported format: \"" +
                expectedDateFormat + "\" Your string: " + providedString);
    }
}
