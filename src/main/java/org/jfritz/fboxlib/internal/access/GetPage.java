package org.jfritz.fboxlib.internal.access;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.jfritz.fboxlib.exceptions.FirmwareNotDetectedException;
import org.jfritz.fboxlib.exceptions.InvalidCredentialsException;
import org.jfritz.fboxlib.exceptions.InvalidSessionIdException;
import org.jfritz.fboxlib.exceptions.PageNotFoundException;
import org.jfritz.fboxlib.fritzbox.FritzBoxCommunication;
import org.jfritz.fboxlib.internal.helper.HttpHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class GetPage {

    private static final String URL_SUPPORT = "/support.lua";
    protected FritzBoxCommunication fbc;

    public GetPage(FritzBoxCommunication fbc) {
        this.fbc = fbc;
    }

    public boolean checkAccess() throws IOException, InvalidCredentialsException, InvalidSessionIdException {
        boolean result = false;
        String resultStr;
        try {
            resultStr = getPageAsString(URL_SUPPORT);
            result = resultStr != null &&
                    (resultStr.toLowerCase().contains("paketmitschnitt")
                            || resultStr.toLowerCase().contains("packet trace")
                            || resultStr.toLowerCase().contains("paquetes")
                            || resultStr.toLowerCase().contains("paquets")
                            || resultStr.toLowerCase().contains("pacchetti"));
        } catch (PageNotFoundException e) {
            // nothing to do here, just proceed with next check
        }
        if (!result) {
            try {
                List<NameValuePair> params = getAdditionalParameters();
                params.add(new BasicNameValuePair("getpage", "../html/" + fbc.getFirmwareVersion().getLanguage() + "/menus/menu2.html"));
                params.add(new BasicNameValuePair("var:lang", fbc.getFirmwareVersion().getLanguage()));
                params.add(new BasicNameValuePair("var:menu", "system"));
                params.add(new BasicNameValuePair("var:pagename", "extended"));

                resultStr = postToPageAndGetAsString(FritzBoxCommunication.URL_WEBCM, params);
                result = resultStr != null && resultStr.toLowerCase().contains("expertenansicht");
            } catch (FirmwareNotDetectedException e1) {
                // we have no firmwareVersion?? Then we'll fail gracefully
                result = false;
            } catch (PageNotFoundException e1) {
                // nothing to do here
                result = false;
            }
        }
        if (!result) {
            try {
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("getpage", "../html/" + fbc.getFirmwareVersion().getLanguage() + "/menus/menu2.html"));
                params.add(new BasicNameValuePair("var:lang", fbc.getFirmwareVersion().getLanguage()));
                params.add(new BasicNameValuePair("var:menu", "system"));
                params.add(new BasicNameValuePair("var:pagename", "extended"));
                params.add(new BasicNameValuePair("login:command/password", fbc.getPassword()));
                resultStr = postToPageAndGetAsString(FritzBoxCommunication.URL_WEBCM, params);
                result = resultStr != null && resultStr.toLowerCase().contains("expertenansicht");
            } catch (FirmwareNotDetectedException e1) {
                // we have no firmwareVersion?? Then we'll fail gracefully
                result = false;
            } catch (PageNotFoundException e1) {
                // nothing to do here
                result = false;
            }
        }
        return result;
    }

    public String getPageAsString(final String url) throws IOException, PageNotFoundException, InvalidCredentialsException, InvalidSessionIdException {
        List<NameValuePair> additionalParameters = getAdditionalParameters();
        String addParStr = "";
        if (url.contains("?")) {
            addParStr = "&";
        } else {
            addParStr = "?";
        }
        for (int i = 0; i < additionalParameters.size(); i++) {
            NameValuePair p = additionalParameters.get(i);
            addParStr = addParStr.concat(p.getName() + "=" + p.getValue());
            if (i < additionalParameters.size() - 1) {
                addParStr = addParStr.concat("&");
            }
        }
        String result = HttpHelper.getInstance(fbc).getHttpContentAsString(fbc.generateUrlPrefix() + url + addParStr);

        checkResponse(result);

        return result;
    }

    protected List<NameValuePair> getAdditionalParameters() {
        return null;
    }

    public String postToPageAndGetAsString(final String url, List<NameValuePair> params) throws IOException, PageNotFoundException, InvalidCredentialsException, InvalidSessionIdException {
        List<NameValuePair> additionalParameters = getAdditionalParameters();
        for (NameValuePair nvp : params) {
            additionalParameters.add(nvp);
        }
        String result = HttpHelper.getInstance(fbc).postToHttpAndGetAsString(fbc.generateUrlPrefix() + url, additionalParameters);

        checkResponse(result);

        return result;
    }

    public Vector<String> postToPageAndGetAsVector(final String url, List<NameValuePair> params) throws IOException, PageNotFoundException, InvalidCredentialsException, InvalidSessionIdException {
        List<NameValuePair> additionalParameters = getAdditionalParameters();
        for (NameValuePair nvp : params) {
            additionalParameters.add(nvp);
        }

        Vector<String> result = HttpHelper.getInstance(fbc).postToHttpAndGetAsVector(fbc.generateUrlPrefix() + url, additionalParameters);

        for (String s : result) {
            checkResponse(s);
        }

        return result;
    }

    private void checkResponse(final String s) throws InvalidCredentialsException {
        if (s.toLowerCase().contains("sid=0000000000000000")) {
//			|| lowercase.contains("uisubmitlogin")
//			|| lowercase.contains("anmeldung")
//			// FIXME: also check for international login data in old firmwares!!!			
            throw new InvalidCredentialsException();
        }
    }
}
