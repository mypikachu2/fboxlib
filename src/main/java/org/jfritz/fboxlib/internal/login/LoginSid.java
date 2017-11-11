package org.jfritz.fboxlib.internal.login;

import org.jfritz.fboxlib.exceptions.InvalidSessionIdException;
import org.jfritz.fboxlib.exceptions.LoginBlockedException;
import org.jfritz.fboxlib.exceptions.PageNotFoundException;
import org.jfritz.fboxlib.fritzbox.FritzBoxCommunication;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginSid {

    protected FritzBoxCommunication fbc;
    protected String loginResponse;
    protected String sid;

    public LoginSid(final FritzBoxCommunication fbc) {
        this.fbc = fbc;
    }

    public boolean login() throws LoginBlockedException, IOException, PageNotFoundException, InvalidSessionIdException {
        int blockTime = 5;
        getLoginSite();
        boolean result = false;
        try {
            result = getSid();
        } catch (LoginBlockedException e) {
            // login is currently blocked, we will retry
            blockTime = Integer.parseInt(e.getRemainingBlockTime());
        }
        if (result) {
            fbc.setSid(sid);
        } else {
            // retry again
            try {
                result = retryLogin(blockTime);
            } catch (LoginBlockedException e) {
                // login is blocked again, try one more time, then give up if login is not possible
                blockTime = Integer.parseInt(e.getRemainingBlockTime());
            }
            if (!result) {
                // and once more, after 3 retries we should give up!
                result = retryLogin(blockTime);
            }
        }

        return result;
    }

    private boolean retryLogin(int blockTime) throws LoginBlockedException, IOException, PageNotFoundException, InvalidSessionIdException {
        if (blockTime > 8) {
            throw new LoginBlockedException(Integer.toString(blockTime));
        }

        //		System.out.println("We are blocked, waiting for " + blockTime + " seconds before retry");

        boolean result = false;
        int wait = 250 + (blockTime * 1000);

        try {
            Thread.sleep(wait);
            getLoginSite();
            result = getSid();
            if (result) {
                fbc.setSid(sid);
                return true;
            } else {
                fbc.invalidateSid();
                return false;
            }
        } catch (InterruptedException e) {
            return false;
        }
    }

    protected void getLoginSite() throws IOException, PageNotFoundException, InvalidSessionIdException {
        System.err.println("Not implemented");
    }

    protected boolean getSid() throws LoginBlockedException, IOException, PageNotFoundException, InvalidSessionIdException {
        System.err.println("Not implemented");
        return false;
    }

    protected String getXmlValue(final String key) {
        String sidRegex = "<" + key + ">([^\"]*)</" + key + ">";
        Pattern sidPattern = Pattern.compile(sidRegex);

        Matcher matcher = sidPattern.matcher(loginResponse);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return null;
        }
    }

    protected String generateResponseFromChallengeAndPassword(final String challenge, final String password) {
        try {
            return challenge + "-" + generateMD5(challenge + "-" + replaceInvalidPasswordCharacters(password));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    private String replaceInvalidPasswordCharacters(final String box_password) {
        String result = box_password;
        // replace all unicodecharacters greater than 255 with the character '.'
        for (int i = 0; i < box_password.length(); i++) {
            int codePoint = box_password.codePointAt(i);
            if (codePoint > 255) {
                result = box_password.substring(0, i) + '.' + box_password.substring(i + 1);
            }
        }
        return result;
    }

    private String generateMD5(String pwd) throws NoSuchAlgorithmException {
        MessageDigest m = MessageDigest.getInstance("MD5");
        String md5Pass = "";
        byte passwordBytes[] = null;
        try {
            passwordBytes = pwd.getBytes("UTF-16LE");
            m.update(passwordBytes, 0, passwordBytes.length);
            md5Pass = new BigInteger(1, m.digest()).toString(16);
        } catch (UnsupportedEncodingException e) {
        }
        return md5Pass;
    }
}
