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

public class LoginSidLua extends LoginSid {
    public static final String URL_LOGIN_SID_LUA = "/login_sid.lua";

    public LoginSidLua(final FritzBoxCommunication fbc) {
        super(fbc);
    }

    @Override
    protected void getLoginSite() throws IOException, PageNotFoundException, InvalidSessionIdException {
        loginResponse = getPageSidLua().toLowerCase();
    }

    @Override
    public boolean getSid() throws LoginBlockedException, IOException, PageNotFoundException, InvalidSessionIdException {
        checkLoginBlocked();

        sid = getXmlValue("sid");
        if ("0000000000000000".equals(sid)) {
            String challengeResponse = generateResponseFromChallengeAndPassword(getXmlValue("challenge"), fbc.getPassword());

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("response", challengeResponse));

            if (!"".equals(fbc.getUserName())) {
                params.add(new BasicNameValuePair("username", fbc.getUserName()));
            }

            loginResponse = HttpHelper.getInstance(fbc).postToHttpAndGetAsString(fbc.generateUrlPrefix() + URL_LOGIN_SID_LUA, params).toLowerCase();
            sid = getXmlValue("sid");
            checkLoginBlocked();
            return !"0000000000000000".equals(sid);
        } else {
            return true;
        }
    }

    private void checkLoginBlocked() throws LoginBlockedException {
        String blockTime;
        blockTime = getXmlValue("blocktime");

        if (blockTime != null && !"0".equals(blockTime)) {
            throw new LoginBlockedException(blockTime);
        }
    }

    public String getPageSidLua() throws IOException, PageNotFoundException, InvalidSessionIdException {
        String result;
        try {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("place", "holder"));
            result = HttpHelper.getInstance(fbc).postToHttpAndGetAsString(fbc.generateUrlPrefix() + URL_LOGIN_SID_LUA, params);
        } catch (ClientProtocolException cpe) {
            throw new ClientProtocolException("Could not get URL " + fbc.generateUrlPrefix() + LoginSidLua.URL_LOGIN_SID_LUA + " " + cpe.getMessage());
        }
        return result;
    }

}
