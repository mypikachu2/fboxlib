package org.jfritz.fboxlib.internal.query;

import org.jfritz.fboxlib.exceptions.InvalidCredentialsException;
import org.jfritz.fboxlib.exceptions.InvalidSessionIdException;
import org.jfritz.fboxlib.exceptions.LoginBlockedException;
import org.jfritz.fboxlib.exceptions.PageNotFoundException;
import org.jfritz.fboxlib.fritzbox.FritzBoxCommunication;

import java.io.IOException;
import java.util.Vector;

public class QueryFactory {

    private static final String QUERY_GET_VERSION = "logic:status/nspver";

    public static Query getQueryMethodForFritzBox(FritzBoxCommunication fritzBox) throws IOException, LoginBlockedException, InvalidCredentialsException, PageNotFoundException {

        Vector<String> query = new Vector<String>();
        query.add(QUERY_GET_VERSION);

        Query queryOld = new QueryTxtOld(fritzBox);
        Query queryNew = new QueryTxtNew(fritzBox);
        Query queryLua = new QueryLua(fritzBox);
        Query queryUnknown = new QueryUnknown();

        boolean result = false;
        try {
            result = checkQuery(queryLua);
            if (result) {
                return queryLua;
            }
        } catch (Exception e) {
            // nothing to do, test of queryLua failed, continue with other checks
        }

        if (!result) {
            try {
                result = checkQuery(queryOld);
                if (result) {
                    return queryOld;
                }
            } catch (Exception e) {
                // nothing to do, test of queryOld failed, continue with other checks
            }
        }

        if (!result) {
            try {
                result = checkQuery(queryNew);
                if (result) {
                    return queryNew;
                }
            } catch (Exception e) {
                // nothing to do, test of queryNew failed, continue with other checks
            }
        }

        return queryUnknown;
    }

    private static boolean checkQuery(Query iquery) throws IOException, LoginBlockedException, InvalidCredentialsException, PageNotFoundException, InvalidSessionIdException {
        Vector<String> query = new Vector<String>();
        query.add(QUERY_GET_VERSION);

        Vector<String> response = iquery.getQuery(query);
        if (response.size() != 0 && !"".equals(response.get(0))) {
            return true;
        }
        return false;
    }
}
