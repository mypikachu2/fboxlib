package org.jfritz.fboxlib.exceptions;

public class PageNotFoundException extends Exception {

    private static final long serialVersionUID = 7372795078750850520L;

    public PageNotFoundException() {
        super();
    }

    public PageNotFoundException(final String param) {
        super(param);
    }
}
