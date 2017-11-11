package org.jfritz.fboxlib.internal.query;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.jfritz.fboxlib.exceptions.InvalidCredentialsException;
import org.jfritz.fboxlib.exceptions.InvalidSessionIdException;
import org.jfritz.fboxlib.exceptions.LoginBlockedException;
import org.jfritz.fboxlib.exceptions.PageNotFoundException;
import org.jfritz.fboxlib.fritzbox.FritzBoxCommunication;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class QueryTxtNew extends IQuery {

    protected FritzBoxCommunication fritzBox;

    public QueryTxtNew(FritzBoxCommunication fritzBox) {
        this.fritzBox = fritzBox;
        this.queryType = QueryType.TXT_NEW;
    }

    public Vector<String> getQuery(Vector<String> queries) throws IOException, LoginBlockedException, InvalidCredentialsException, PageNotFoundException, InvalidSessionIdException {
        Vector<String> response = new Vector<String>();

        if (fritzBox == null) {
            // TODO log error message
            return response;
        }

        List<NameValuePair> postdata = generatePostDataNew(queries);

        response = fritzBox.postToPageAndGetAsVector(FritzBoxCommunication.URL_WEBCM, postdata);

        if (response.size() != 0) {
            response.remove(response.size() - 1); // letzte Zeile entfernen (leerzeile)
        }

        return response;
    }

    private final List<NameValuePair> generatePostDataNew(Vector<String> queries) {
        List<NameValuePair> postdata = new ArrayList<NameValuePair>();
        postdata.add(new BasicNameValuePair("getpage", "../html/query.txt"));

        if (fritzBox == null) {
            // TODO log error
            return postdata;
        }

        for (int i = 0; i < queries.size(); i++) {
            postdata.add(new BasicNameValuePair("var:n[" + i + "]", queries.get(i)));
        }

        return postdata;
    }
}
