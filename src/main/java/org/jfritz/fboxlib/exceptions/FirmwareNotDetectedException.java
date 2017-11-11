package org.jfritz.fboxlib.exceptions;

public class FirmwareNotDetectedException extends Exception {

    private static final long serialVersionUID = 7372795078750850520L;

    public FirmwareNotDetectedException() {
        super();
    }

    public FirmwareNotDetectedException(final String param) {
        super(param);
    }
}
