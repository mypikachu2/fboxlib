package org.jfritz.fboxlib.exceptions;

public class InvalidCredentialsException extends Exception {

    private static final long serialVersionUID = 7372795078750850520L;

    public InvalidCredentialsException() {
        super("Invalid credentials");
    }

    public InvalidCredentialsException(final String param) {
        super(param);
    }
}
