package org.jfritz.fboxlib.internal.login;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;
import org.jfritz.fboxlib.exceptions.InvalidSessionIdException;
import org.jfritz.fboxlib.exceptions.LoginBlockedException;
import org.jfritz.fboxlib.exceptions.PageNotFoundException;
import org.jfritz.fboxlib.fritzbox.FritzBoxCommunication;
import org.jfritz.fboxlib.internal.helper.HttpHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginSidXml extends LoginSid {
    private static final String SID_XML = "../html/login_sid.xml";
    private static final String PATTERN_SID = "name=\"sid\" value=\"([^\"]*)\"";
    private static final String PATTERN_BLOCKED_1 = "doblocklogin\\s*\\(([\\d]*)\\)";
    private static final String PATTERN_BLOCKED_2 = "loginblocked\\s*=\\s*parseint\\s*\\(\\s*\"([\\d]*)\",\\s*10\\s*\\)";

    private Pattern sidPattern;
    private Pattern blockedPattern1;
    private Pattern blockedPattern2;
    private Matcher m;

    private final static String[] POSTDATA_ACCESS_METHOD = {
            "../html/de/menus/menu2.html", //$NON-NLS-1$
            "../html/en/menus/menu2.html", //$NON-NLS-1$
            "../html/menus/menu2.html"}; //$NON-NLS-1$

    public LoginSidXml(final FritzBoxCommunication fbc) {
        super(fbc);
        sidPattern = Pattern.compile(PATTERN_SID);
        blockedPattern1 = Pattern.compile(PATTERN_BLOCKED_1);
        blockedPattern2 = Pattern.compile(PATTERN_BLOCKED_2);
    }

    @Override
    protected void getLoginSite() throws IOException, PageNotFoundException, InvalidSessionIdException {
        loginResponse = getPageSidXml().toLowerCase();
    }

    @Override
    public boolean getSid() throws LoginBlockedException, IOException, PageNotFoundException, InvalidSessionIdException {
        String iswriteaccess = "";
        iswriteaccess = getXmlValue("iswriteaccess");

        if (iswriteaccess != null && "1".equals(iswriteaccess)) {
            sid = getXmlValue("sid");
            return !"0000000000000000".equals(sid);
        } else {
            sid = getXmlValue("sid");
            if ("0000000000000000".equals(sid)) {
                String challengeResponse = generateResponseFromChallengeAndPassword(getXmlValue("challenge"), fbc.getPassword());

                List<NameValuePair> params = new ArrayList<NameValuePair>();
                for (int i = 0; i < POSTDATA_ACCESS_METHOD.length; i++) {
                    if (!sid.equals("0000000000000000")) {
                        return true;
                    }

                    params.add(new BasicNameValuePair("getpage", POSTDATA_ACCESS_METHOD[i]));
                    params.add(new BasicNameValuePair("var:lang", "de"));
                    params.add(new BasicNameValuePair("var:menu", "home"));
                    params.add(new BasicNameValuePair("var:pagename", "home"));
                    params.add(new BasicNameValuePair("login:command/response", challengeResponse));

                    if (!"".equals(fbc.getUserName())) {
                        params.add(new BasicNameValuePair("username", fbc.getUserName()));
                    }

                    loginResponse = HttpHelper.getInstance(fbc).postToHttpAndGetAsString(fbc.generateUrlPrefix() + FritzBoxCommunication.URL_WEBCM, params).toLowerCase();
                    extractSidFromResponse();

                    if (sid.equals("0000000000000000")) {
                        checkForLoginBlocked();
                    }
                }
            }
            return !"".equals(sid) && !"0000000000000000".equals(sid);
        }
    }

    private void extractSidFromResponse() {
        m = sidPattern.matcher(loginResponse);
        if (m.find()) {
            sid = m.group(1);
        }
    }

    private void checkForLoginBlocked() throws LoginBlockedException {
        // checking for blocked login
        boolean foundBlocked = false;

        foundBlocked = checkForLoginBlockedPattern1();

        if (!foundBlocked) {
            foundBlocked = checkForLoginBlockedPattern2();
        }
    }

    private boolean checkForLoginBlockedPattern2() throws LoginBlockedException {
        boolean foundBlocked = false;
        m = blockedPattern2.matcher(loginResponse);
        try {
            if (m.find()) {
                foundBlocked = true;
                Integer.parseInt(m.group(1));
                throw new LoginBlockedException(m.group(1));
            }
        } catch (NumberFormatException nfe) {
            // nothing to do here, proceed with next "login blocked"-detection
        }
        return foundBlocked;
    }

    private boolean checkForLoginBlockedPattern1() throws LoginBlockedException {
        boolean foundBlocked = false;
        m = blockedPattern1.matcher(loginResponse);
        try {
            if (m.find()) {
                foundBlocked = true;
                int blockTime = Integer.parseInt(m.group(1)) / 1000;

                throw new LoginBlockedException(Integer.toString(blockTime));
            }
        } catch (NumberFormatException nfe) {
            // nothing to do here, proceed with next "login blocked"-detection
        }
        return foundBlocked;
    }

    private String getPageSidXml() throws IOException, PageNotFoundException, InvalidSessionIdException {
        String result;
        try {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("getpage", SID_XML));
            result = HttpHelper.getInstance(fbc).postToHttpAndGetAsString(fbc.generateUrlPrefix() + FritzBoxCommunication.URL_WEBCM, params);
        } catch (ClientProtocolException cpe) {
            throw new ClientProtocolException("Could not get URL " + fbc.generateUrlPrefix() + FritzBoxCommunication.URL_WEBCM + " " + SID_XML + ": " + cpe.getMessage());
        }
        return result;
    }

}
