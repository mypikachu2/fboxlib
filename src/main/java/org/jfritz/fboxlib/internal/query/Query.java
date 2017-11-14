package org.jfritz.fboxlib.internal.query;

import org.jfritz.fboxlib.exceptions.InvalidCredentialsException;
import org.jfritz.fboxlib.exceptions.InvalidSessionIdException;
import org.jfritz.fboxlib.exceptions.LoginBlockedException;
import org.jfritz.fboxlib.exceptions.PageNotFoundException;

import java.io.IOException;
import java.util.Vector;

public class Query implements IQuery {
    protected QueryType queryType = QueryType.UNKNOWN;

    @Override
    public Vector<String> getQuery(Vector<String> queries) throws IOException, LoginBlockedException, InvalidCredentialsException, PageNotFoundException, InvalidSessionIdException {
        // not implemented
        return null;
    }

    public String toString() {
        return queryType.toString();
    }

    public QueryType getQueryType() {
        return queryType;
    }
}
