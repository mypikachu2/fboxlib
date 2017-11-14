package org.jfritz.fboxlib.internal.query;

import org.jfritz.fboxlib.exceptions.*;
import org.jfritz.fboxlib.fritzbox.FritzBoxCommunication;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.Vector;

public class QueryTest {

    private FritzBoxCommunication fbc;

    @Before
    public void setup() throws InvalidCredentialsException, LoginBlockedException, IOException, PageNotFoundException, FirmwareNotDetectedException {
        fbc = new FritzBoxCommunication("http", "fritz.box", "80");
        fbc.setUserName("jfritz-test-user");
        fbc.setPassword("jfritz-te$t-password");
        fbc.detectLoginMethod();
        fbc.getFirmwareVersion();
        if (!fbc.login()) {
            System.err.println("Could not login ...");
        }
    }

    @Ignore
    @Test
    public void test() throws IOException, LoginBlockedException, InvalidCredentialsException, PageNotFoundException, InvalidSessionIdException {
        Query q = fbc.getQueryMethodForFritzBox();
        Vector<String> queries = new Vector<String>();
        queries.add("box:status/localtime");

        Vector<String> results = q.getQuery(queries);
        for (String s : results) {
            System.out.println(s);
        }
    }
}
