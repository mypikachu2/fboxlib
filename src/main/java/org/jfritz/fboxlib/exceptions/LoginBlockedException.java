package org.jfritz.fboxlib.exceptions;

public class LoginBlockedException extends Exception {

    private static final long serialVersionUID = 7372795078750850520L;
    private String blockTimeRemaining = "0";

    public LoginBlockedException(final String blockTimeRemaining) {
        super("Login is currently blocked for " + blockTimeRemaining + " seconds");
        this.blockTimeRemaining = blockTimeRemaining;
    }

    public String getRemainingBlockTime() {
        return blockTimeRemaining;
    }
}
