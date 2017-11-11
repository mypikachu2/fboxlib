package org.jfritz.fboxlib.internal.access;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.jfritz.fboxlib.fritzbox.FritzBoxCommunication;

import java.util.ArrayList;
import java.util.List;

public class GetPageWithSid extends GetPage {

    public String sid = null;

    public GetPageWithSid(FritzBoxCommunication fbc) {
        super(fbc);
    }

    @Override
    protected List<NameValuePair> getAdditionalParameters() {
        List<NameValuePair> result = new ArrayList<NameValuePair>();
        result.add(new BasicNameValuePair("sid", this.sid));
        return result;
    }

    public void setSid(final String sid) {
        this.sid = sid;
    }

    public String getSid() {
        return this.sid;
    }

    public void invalidateSid() {
        this.sid = "0000000000000000";
    }
}
