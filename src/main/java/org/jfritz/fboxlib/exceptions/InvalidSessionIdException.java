package org.jfritz.fboxlib.exceptions;

public class InvalidSessionIdException extends Exception {

    private static final long serialVersionUID = 7372795078750850520L;

    public InvalidSessionIdException() {
        super("Invalid session ID");
    }

    public InvalidSessionIdException(final String param) {
        super(param);
    }
}
