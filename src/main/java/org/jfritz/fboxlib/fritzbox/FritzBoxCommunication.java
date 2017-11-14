package org.jfritz.fboxlib.fritzbox;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;
import org.jfritz.fboxlib.enums.ConnectionState;
import org.jfritz.fboxlib.enums.LoginMethod;
import org.jfritz.fboxlib.enums.LoginMode;
import org.jfritz.fboxlib.enums.LoginState;
import org.jfritz.fboxlib.exceptions.*;
import org.jfritz.fboxlib.internal.access.GetPage;
import org.jfritz.fboxlib.internal.access.GetPageWithPassword;
import org.jfritz.fboxlib.internal.access.GetPageWithSid;
import org.jfritz.fboxlib.internal.helper.HttpHelper;
import org.jfritz.fboxlib.internal.login.FritzLogin;
import org.jfritz.fboxlib.internal.query.IQuery;
import org.jfritz.fboxlib.internal.query.Query;
import org.jfritz.fboxlib.internal.query.QueryFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class FritzBoxCommunication implements IQuery {

    public static final String URL_WEBCM = "/cgi-bin/webcm";

    protected HttpHelper httpHelper = HttpHelper.getInstance(this);

    private String protocol = "http";
    private String host = "fritz.box";
    private String port = "80";
    private String upnpport = "49000";

    private String username = "";
    private String password = "";

    private ConnectionState connectionState = ConnectionState.NOT_CONNECTED;
    private LoginState loginState = LoginState.NOT_LOGGED_IN;

    private LoginMethod loginMethod;
    private FritzLogin fritzLogin;
    private GetPage getPage;
    private Query query;

    private NetworkMethods networkMethods;

    private final String URL_SYSTEM_STATUS = "/cgi-bin/system_status";
    private final String URL_JSON_BOXINFO = "/jason_boxinfo.xml";

    public FritzBoxCommunication() {
        this("http", "fritz.box", "80");
    }

    public FritzBoxCommunication(String protocol, String host, String port) {
        this.protocol = protocol;
        this.host = host;
        this.port = port;

        fritzLogin = new FritzLogin();
        networkMethods = new NetworkMethods(this);
    }

    public String getUserName() {
        return username;
    }

    public void setUserName(final String username) {
        invalidateStatus();
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String pwd) {
        invalidateStatus();
        this.password = pwd;
    }

    public void getNewSid() throws InvalidCredentialsException, LoginBlockedException, IOException, PageNotFoundException {
        loginState = LoginState.NOT_LOGGED_IN;
        invalidateSid();
        login();
    }

    public void invalidateStatus() {
        loginState = LoginState.NOT_LOGGED_IN;
        loginMethod = null;
        query = null;
    }

    public String generateUrlPrefix() {
        return protocol + "://" + host + ":" + port;
    }

    public String generateUpnpUrlPrefix() {
        return protocol + "://" + host + ":" + upnpport;
    }

    public String getSystemStatus() throws IOException, PageNotFoundException {
        String result = "";
        try {
            result = httpHelper.postToHttpAndGetAsString(generateUrlPrefix() + URL_SYSTEM_STATUS, null);
        } catch (InvalidSessionIdException isside) {
            // should not be thrown, because SystemStatus is not protected
        } catch (ClientProtocolException cpe) {
            throw new ClientProtocolException("Could not get system status. " + generateUrlPrefix() + URL_SYSTEM_STATUS + " " + cpe.getMessage());
        }
        return result;
    }

    public String getJsonBoxInfo() throws IOException, PageNotFoundException, FirmwareNotDetectedException {
        String result = "";

        FirmwareVersion fw = getFirmwareVersion();
        if (fw != null) {
            if (fw.isLowerThan(4, 80)) {
                return result;
            } else {
                try {
                    result = httpHelper.postToHttpAndGetAsString(generateUrlPrefix() + URL_JSON_BOXINFO, null);
                } catch (InvalidSessionIdException isside) {
                    // should not be thrown, because json_boxinfo.xml is not protected
                } catch (ClientProtocolException cpe) {
                    throw new ClientProtocolException("Could not get json_boxinfo.xml. " + generateUrlPrefix() + URL_JSON_BOXINFO + " " + cpe.getMessage());
                }
            }
        }
        return result;
    }

    public FirmwareVersion getFirmwareVersion() throws IOException, FirmwareNotDetectedException, PageNotFoundException {
        SystemStatus systemStatus = new SystemStatus();
        return systemStatus.getFWVersionFromSystemStatus(this);
    }

    public void setSid(final String sid) {
        loginState = LoginState.LOGGED_IN;
        if (getPage instanceof GetPageWithSid) {
            ((GetPageWithSid) getPage).setSid(sid);
        }
    }

    public String getSid() {
        if (getPage instanceof GetPageWithSid) {
            return ((GetPageWithSid) getPage).getSid();
        } else {
            return "NO_SID_AVAILABLE";
        }
    }

    public void invalidateSid() {
        if (getPage instanceof GetPageWithSid) {
            ((GetPageWithSid) getPage).invalidateSid();
        }
    }

    public Vector<String> getStartPage() {
        Vector<String> result = new Vector<String>();

        String url = generateUrlPrefix() + "/";
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("a", "b"));

        try {
            result = httpHelper.postToHttpAndGetAsVector(url, params);
        } catch (Exception e) {
            invalidateStatus();
            invalidateSid();
        }

        Pattern p = Pattern.compile("<frame src=\"([^\"]*)\"");
        for (String s : result) {
            Matcher m = p.matcher(s);
            if (m.find() && m.groupCount() > 0) {
                String frame = m.group(1);
                url = generateUrlPrefix() + frame;
                try {
                    result = httpHelper.postToHttpAndGetAsVector(url, params);
                } catch (Exception e) {
                    invalidateStatus();
                    invalidateSid();
                }
                break;
            }
        }

        return result;
    }

    public String getUpnpUrlAsString(final String url) throws IOException, LoginBlockedException, InvalidCredentialsException, PageNotFoundException, InvalidSessionIdException {
        ensureWeAreLoggedIn();

        try {
            return HttpHelper.getInstance(this).getHttpContentAsString(generateUpnpUrlPrefix() + url);
        } catch (InvalidSessionIdException e) {
            // get new SID and try again, if this fails again, throw exception
            getNewSid();
            return HttpHelper.getInstance(this).getHttpContentAsString(generateUpnpUrlPrefix() + url);
        }
    }

    public String getPageAsString(final String url) throws IOException, LoginBlockedException, InvalidCredentialsException, PageNotFoundException, InvalidSessionIdException {
        ensureWeAreLoggedIn();

        try {
            return getPage.getPageAsString(url);
        } catch (InvalidSessionIdException e) {
            // get new SID and try again, if this fails again, throw exception
            getNewSid();
            return getPage.getPageAsString(url);
        } catch (InvalidCredentialsException e) {
            // get new SID and try again, if this fails again, throw exception
            invalidateStatus();
            invalidateSid();
            login();
            return getPage.getPageAsString(url);
        }
    }

    public String postToPageAndGetAsString(final String url, List<NameValuePair> params) throws IOException, LoginBlockedException, InvalidCredentialsException, PageNotFoundException, InvalidSessionIdException {
        ensureWeAreLoggedIn();

        try {
            return getPage.postToPageAndGetAsString(url, params);
        } catch (InvalidSessionIdException e) {
            // get new SID and try again, if this fails again, throw exception
            getNewSid();
            return getPage.postToPageAndGetAsString(url, params);
        } catch (InvalidCredentialsException e) {
            // get new SID and try again, if this fails again, throw exception
            invalidateStatus();
            invalidateSid();
            login();
            return getPage.postToPageAndGetAsString(url, params);
        }
    }

    public Vector<String> postToPageAndGetAsVector(final String url, List<NameValuePair> params) throws IOException, LoginBlockedException, InvalidCredentialsException, PageNotFoundException, InvalidSessionIdException {
        ensureWeAreLoggedIn();

        try {
            return getPage.postToPageAndGetAsVector(url, params);
        } catch (InvalidSessionIdException e) {
            // get new SID and try again, if this fails again, throw exception
            getNewSid();
            return getPage.postToPageAndGetAsVector(url, params);
        } catch (InvalidCredentialsException e) {
            // get new SID and try again, if this fails again, throw exception
            invalidateStatus();
            invalidateSid();
            login();
            return getPage.postToPageAndGetAsVector(url, params);
        }
    }

    public boolean isConnected() {
        return connectionState == ConnectionState.CONNECTED;
    }

    public boolean checkConnection() throws NoConnectionToBox {
        try {
            getSystemStatus();
            connectionState = ConnectionState.CONNECTED;
            return true;
        } catch (ClientProtocolException e) {
            connectionState = ConnectionState.NOT_CONNECTED;
            e.printStackTrace();
            return false;
        } catch (PageNotFoundException e) {
            connectionState = ConnectionState.NOT_CONNECTED;
            return false;
        } catch (IOException e) {
            connectionState = ConnectionState.NOT_CONNECTED;
            throw new NoConnectionToBox(e.getMessage());
        }
    }

    public boolean isLoginMethodValid() {
        return loginMethod != null;
    }

    public LoginMethod detectLoginMethod() throws IOException, PageNotFoundException {
        setLoginMethod(fritzLogin.detectLoginMethod(this));
        return loginMethod;
    }

    public LoginMode getLoginMode() {
        return fritzLogin.getLoginMode();
    }

    public String getLastUserName() {
        return fritzLogin.getLastUserName();
    }

    public boolean isLoggedIn() {
        return loginState == LoginState.LOGGED_IN;
    }

    public boolean checkAccess() {
        try {
            if (getPage == null) {
                return false;
            }
            return getPage.checkAccess();
        } catch (InvalidSessionIdException e) {
            // get new SID and try again, if this fails again, throw exception
            try {
                getNewSid();
                return getPage.checkAccess();
            } catch (Exception e1) {
                return false;
            }
        } catch (InvalidCredentialsException e) {
            return false;
        } catch (ClientProtocolException e) {
            return false;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean login() throws InvalidCredentialsException, LoginBlockedException, IOException, PageNotFoundException {
        if (isLoggedIn()) {
            return true;
        }

        if (!isLoginMethodValid()) {
            detectLoginMethod();
        }

        try {
            fritzLogin.doLogin(this);
        } catch (InvalidSessionIdException e) {
            loginState = LoginState.INVALID_CREDENTIALS;
            throw new InvalidCredentialsException();
        }

        if (!fritzLogin.isLoginValid()) {
            loginState = LoginState.INVALID_CREDENTIALS;
            throw new InvalidCredentialsException();
        }

        boolean result = fritzLogin.isLoginValid();
        if (result) {
            loginState = LoginState.LOGGED_IN;
        } else {
            loginState = LoginState.NOT_LOGGED_IN;
        }

        return result;
    }

    private void ensureWeAreLoggedIn() throws IOException,
            LoginBlockedException, InvalidCredentialsException, PageNotFoundException {
        if (!isLoggedIn()) {
            login();
        }
    }

    public void setLoginMethod(final LoginMethod lm) {
        if (this.loginMethod == null || this.loginMethod != lm) {
            switch (lm) {
                case SidLua:
                    getPage = new GetPageWithSid(this);
                    break;
                case SidXml:
                    getPage = new GetPageWithSid(this);
                    break;
                case PlainPassword:
                    getPage = new GetPageWithPassword(this);
                    break;
            }
        }
        this.loginMethod = lm;
    }

    public Query getQueryMethodForFritzBox() throws IOException, LoginBlockedException, InvalidCredentialsException, PageNotFoundException {
        if (query == null) {
            query = QueryFactory.getQueryMethodForFritzBox(this);
        }

        return query;
    }

    @Override
    public Vector<String> getQuery(Vector<String> queries) throws IOException, LoginBlockedException, InvalidCredentialsException, PageNotFoundException, InvalidSessionIdException {
        if (query == null) {
            query = QueryFactory.getQueryMethodForFritzBox(this);
        }

        if (query != null) {
            try {
                return query.getQuery(queries);
            } catch (InvalidSessionIdException e) {
                getNewSid();
                return query.getQuery(queries);
            }
        } else {
            return new Vector<String>();
        }
    }

    public NetworkMethods getNetworkMethods() {
        return networkMethods;
    }

}
