package org.jfritz.fboxlib.exceptions;

public class NoConnectionToBox extends Exception {

    private static final long serialVersionUID = 7372795078750850520L;

    public NoConnectionToBox() {
        super();
    }

    public NoConnectionToBox(final String param) {
        super(param);
    }
}
