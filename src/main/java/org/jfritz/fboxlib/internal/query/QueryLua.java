package org.jfritz.fboxlib.internal.query;

import org.jfritz.fboxlib.exceptions.InvalidCredentialsException;
import org.jfritz.fboxlib.exceptions.InvalidSessionIdException;
import org.jfritz.fboxlib.exceptions.LoginBlockedException;
import org.jfritz.fboxlib.exceptions.PageNotFoundException;
import org.jfritz.fboxlib.fritzbox.FritzBoxCommunication;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.Vector;

public class QueryLua extends IQuery {

    private static final String QUERY_LUA = "/query.lua";
    // 06.11.2017 Ich habe den Wert auf 80 gesetzt, da ich ein Problem mit einer alten
    // Firmware xxx.04.84 hatte da ging das Abholen der Anrufliste erst Feherfrei
    // wenn ich den Wert hier mindestens auf 80 gesetzt hatte
    private static final int MAX_QUERIES_PER_REQUEST = 80;

    protected FritzBoxCommunication fritzBox;
    private JSONParser jsonParser;

    public QueryLua(FritzBoxCommunication fritzBox) {
        this.fritzBox = fritzBox;
        this.queryType = QueryType.LUA;
        jsonParser = new JSONParser();
    }

    public Vector<String> getQuery(Vector<String> queries) throws IOException, LoginBlockedException, InvalidCredentialsException, PageNotFoundException, InvalidSessionIdException {
        Vector<String> response = new Vector<String>();

        if (fritzBox == null) {
            // TODO log error message
            return response;
        }

        // to prevent a too long GET url we split our request here in batches of MAX_QUERIES_PER_REQUEST
        int numIterations = queries.size() / MAX_QUERIES_PER_REQUEST;
        Vector<String> subqueries = new Vector<String>();
        for (int i = 0; i < numIterations + 1; i++) {
            subqueries.clear();
            for (int j = 0; j < MAX_QUERIES_PER_REQUEST; j++) {
                int currentElementIndex = j + (i * MAX_QUERIES_PER_REQUEST);
                if (currentElementIndex < queries.size()) {
                    subqueries.add(queries.get(j + (i * MAX_QUERIES_PER_REQUEST)));
                }
            }

            String getData = generateGetData(subqueries);
            String r = fritzBox.getPageAsString(QUERY_LUA + getData);
            try {
                response.addAll(parseResponseToVectorString(r));
            } catch (ParseException e) {
                // TODO log
            }
        }


        return response;
    }

    public Vector<String> parseResponseToVectorString(String input) throws ParseException {
        Vector<String> result = new Vector<String>();
        JSONObject object;
        object = (JSONObject) jsonParser.parse(input);
        for (int i = 0; i < MAX_QUERIES_PER_REQUEST; i++) {
            String s = (String) object.get(Integer.toString(i));
            if (s == null) {
                // no more input, just return
                return result;
            } else {
                result.add(s);
            }
        }
        return result;
    }

    private final String generateGetData(Vector<String> queries) {
        StringBuilder sb = new StringBuilder();

        if (fritzBox == null) {
            // TODO log error
            return sb.toString();
        }

        for (int i = 0; i < queries.size(); i++) {
            if (i == 0) {
                sb.append("?");
            } else {
                sb.append("&");
            }
            sb.append(Integer.toString(i));
            sb.append("=");
            sb.append(queries.get(i));
        }

        return sb.toString();
    }
}
