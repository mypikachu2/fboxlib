package org.jfritz.fboxlib.internal.access;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.jfritz.fboxlib.fritzbox.FritzBoxCommunication;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class GetPageWithPassword extends GetPage {

    public GetPageWithPassword(FritzBoxCommunication fbc) {
        super(fbc);
    }

    @Override
    protected List<NameValuePair> getAdditionalParameters() {
        if (fbc.getPassword() == null || "".equals(fbc.getPassword())) {
            return null;
        } else {
            List<NameValuePair> result = new ArrayList<NameValuePair>();
            try {
                result.add(new BasicNameValuePair("login:command/password", URLEncoder.encode(fbc.getPassword(), "ISO-8859-1")));
            } catch (UnsupportedEncodingException uee) {
                result.add(new BasicNameValuePair("login:command/password", fbc.getPassword()));
            }
            return result;
        }
    }
}
