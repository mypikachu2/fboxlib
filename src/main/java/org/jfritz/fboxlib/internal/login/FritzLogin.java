package org.jfritz.fboxlib.internal.login;

import org.apache.http.client.ClientProtocolException;
import org.jfritz.fboxlib.enums.LoginMethod;
import org.jfritz.fboxlib.enums.LoginMode;
import org.jfritz.fboxlib.exceptions.FirmwareNotDetectedException;
import org.jfritz.fboxlib.exceptions.InvalidSessionIdException;
import org.jfritz.fboxlib.exceptions.LoginBlockedException;
import org.jfritz.fboxlib.exceptions.PageNotFoundException;
import org.jfritz.fboxlib.fritzbox.FirmwareVersion;
import org.jfritz.fboxlib.fritzbox.FritzBoxCommunication;
import org.jfritz.fboxlib.fritzbox.SystemStatus;

import java.io.IOException;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FritzLogin {
    // Das fehlt ab Firmware Version 146.06.25 in der FRITZ!Box 7430 login Seite
    private static final String PATTERN_COMP_MODE = "\"boxusers:settings/compatibility_mode\"[^=]*=\\s\"(\\d)\"";
    private static final String PATTERN_LAST_USERNAME = "\"boxusers:settings/last_homenetwork_username\"[^=]*=\\s\"([^\"]*)\"";

    // Geaendert zu ab Firmware Version xxx.06.25
    private static final String PATTERN_COMP_MODE_0625 =
            "<select[\\s| ]id=\"uiViewUser\"[\\s| ]name=\"uiUser\"[\\s| ]tabindex=\"([01]*)\">";
    private static final String PATTERN_LAST_USERNAME_0625 =
            "<input[\\s| ]type=\"hidden\"[\\s| ]id=\"username\"[\\s| ]name=\"username\"[\\s| ]value=\"([^\"]*)\">";

    // "showUser": true, FB 7490 113.06.35-30987
    private static final String PATTERN_COMP_MODE_0635 = "\"showUser\"[^:]*:[\\s| ]*([tT]rue|[fF]alse*),";
    private static final String PATTERN_LAST_USERNAME_0635 = "\"username\"[^:]*:[\\s| ]*\"([^\"]*)\",";

    protected FirmwareVersion fwVersion;
    private LoginMethod loginMethod;
    private LoginMode loginMode = LoginMode.PASSWORD;
    private boolean loginValid = false;
    private String lastUserName = "";

    public LoginMethod detectLoginMethod(final FritzBoxCommunication fbc) throws IOException, PageNotFoundException {
        SystemStatus systemStatus = new SystemStatus();
        loginMode = LoginMode.PASSWORD;
        try {
            fwVersion = systemStatus.getFWVersionFromSystemStatus(fbc);
            loginMethod = getLoginMethodByFirmware(fwVersion);

            if (loginMethod == LoginMethod.SidLua) {
                detectLoginMode(fbc);
            }

            return loginMethod;
        } catch (FirmwareNotDetectedException fnfe) {
            System.err.println(fnfe.getMessage());
        } catch (ClientProtocolException cpe) {
            System.err.println(cpe.getMessage());
        }

        return null;
    }

    public LoginMode getLoginMode() {
        return loginMode;
    }

    public String getLastUserName() {
        return lastUserName;
    }

    protected LoginMethod getLoginMethodByFirmware(final FirmwareVersion fw) {
        if (fw.isLowerThan(04, 74)) {
            return LoginMethod.PlainPassword;
        } else if (fw.isLowerThan(05, 50)) {
            return LoginMethod.SidXml;
        } else {
            return LoginMethod.SidLua;
        }
    }

    protected void detectLoginMode(final FritzBoxCommunication fbc) {
        Vector<String> startPage = fbc.getStartPage();
        loginMode = LoginMode.PASSWORD;
        boolean extracted = false;
        for (String s : startPage) {
            extracted = extractLoginMode(s);
            if (extracted) {
                break;
            }
        }

        if (loginMode == LoginMode.USERNAME_PASSWORD) {
            extracted = false;
            for (String s : startPage) {
                extracted = extractLastUsername(s);
                if (extracted) {
                    break;
                }
            }
        }
    }

    protected boolean extractLoginMode(String line) {
        if (line == null) {
            return false;
        }

        if ("".equals(line)) {
            return false;
        }

        Pattern p = null;

        if (fwVersion != null && fwVersion.isLowerThan(6, 25)) {
            p = Pattern.compile(PATTERN_COMP_MODE);
        } else if (fwVersion != null && fwVersion.isLowerThan(6, 35)) {//&& (fwVersion.getBoxType() != 113)) {
            // FB 7430 146.06.25 / xxx.06.25
            p = Pattern.compile(PATTERN_COMP_MODE_0625);
        } else {
            // "showUser": true, FB 7490 113.06.35-30987
            p = Pattern.compile(PATTERN_COMP_MODE_0635);
        }

        Matcher m = p.matcher(line);
        if (m.find()) {
            if (m.groupCount() > 0) {
                String res = m.group(1);

                if (fwVersion != null && fwVersion.isLowerThan(6, 25)) {
                    if ("0".equals(res)) {
                        loginMode = LoginMode.USERNAME_PASSWORD;
                    } else if ("1".equals(res)) {
                        loginMode = LoginMode.PASSWORD;
                    } else {
                        loginMode = LoginMode.PASSWORD;
                    }
                } else {
                    if ("1".equals(res) || "true".equals(res.toLowerCase())) {
                        loginMode = LoginMode.USERNAME_PASSWORD;
                    } else if ("0".equals(res) || "false".equals(res.toLowerCase())) {
                        loginMode = LoginMode.PASSWORD;
                    } else {
                        loginMode = LoginMode.PASSWORD;
                    }
                }
                return true;
            }
        } else {
            loginMode = LoginMode.PASSWORD;
        }

        return false;
    }

    protected boolean extractLastUsername(String line) {
        lastUserName = "";

        if (line == null) {
            return false;
        }

        if ("".equals(line)) {
            return false;
        }

        Pattern p = null;

        if (fwVersion != null && fwVersion.isLowerThan(6, 25)) {
            p = Pattern.compile(PATTERN_LAST_USERNAME);
        } else if (fwVersion != null && fwVersion.isLowerThan(6, 35)) {// && (fwVersion.getBoxType() != 113)) {
            p = Pattern.compile(PATTERN_LAST_USERNAME_0625);
        } else {
            // "username": "user", FB 7490 113.06.35-30987
            p = Pattern.compile(PATTERN_LAST_USERNAME_0635);
        }

        Matcher m;
        m = p.matcher(line);
        if (m.find()) {
            if (m.groupCount() > 0) {
                lastUserName = m.group(1);
                return true;
            }
        }
        return false;
    }

    public void doLogin(final FritzBoxCommunication fbc) throws LoginBlockedException, IOException, PageNotFoundException, InvalidSessionIdException {
        boolean loginResult = false;
        switch (loginMethod) {
            case SidLua:
                LoginSid lsl = new LoginSidLua(fbc);
                loginResult = lsl.login();
                break;
            case SidXml:
                LoginSid lsx = new LoginSidXml(fbc);
                loginResult = lsx.login();
                break;
            case PlainPassword:
                loginResult = true;
                break;
        }

        if (loginResult) {
            switch (loginMethod) {
                case PlainPassword:
                    loginResult = fbc.checkAccess();
                    break;
                case SidLua: // same as for SidXml
                case SidXml:
                    if (!"0000000000000000".equals(fbc.getSid())) {
                        loginResult = true;
                        break;
                    }
            }
        }

        loginValid = loginResult;
    }

    public boolean isLoginMethodValid() {
        return loginMethod != null;
    }

    public boolean isLoginValid() {
        return loginValid;
    }

    public void invalidateLogin() {
        loginValid = false;
    }
}
