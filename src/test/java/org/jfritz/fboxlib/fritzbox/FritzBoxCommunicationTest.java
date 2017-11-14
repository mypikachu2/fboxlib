package org.jfritz.fboxlib.fritzbox;

import junit.framework.Assert;
import org.jfritz.fboxlib.enums.LoginMethod;
import org.jfritz.fboxlib.enums.LoginMode;
import org.jfritz.fboxlib.exceptions.InvalidCredentialsException;
import org.jfritz.fboxlib.exceptions.InvalidSessionIdException;
import org.jfritz.fboxlib.exceptions.LoginBlockedException;
import org.jfritz.fboxlib.exceptions.PageNotFoundException;
import org.jfritz.fboxlib.internal.query.QueryType;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.Vector;

import static org.junit.Assert.fail;

public class FritzBoxCommunicationTest {

    @Ignore
    @Test
    public void testGetStartPage() {
        // Extracts also HTTP frames
        FritzBoxCommunication fbc = new FritzBoxCommunication("http", "fritz.box", "80");
        Vector<String> result = fbc.getStartPage();
        Assert.assertNotNull(result);
    }

    @Ignore
    @Test
    public void testLoginMethodAndLoginMode() throws IOException, PageNotFoundException {
        FritzBoxCommunication fbc = new FritzBoxCommunication("http", "fritz.box", "80");
        LoginMethod loginMethod = fbc.detectLoginMethod();
        LoginMode loginMode = fbc.getLoginMode();

        Assert.assertNotNull(loginMethod);
        Assert.assertNotNull(loginMode);
    }

    @Ignore
    @Test
    public void testQuery() throws IOException, PageNotFoundException {
        FritzBoxCommunication fbc = new FritzBoxCommunication("http", "fritz.box", "80");
        LoginMethod loginMethod = fbc.detectLoginMethod();
        LoginMode loginMode = fbc.getLoginMode();
        fbc.setUserName("jfritz-test-user");
        fbc.setPassword("jfritz-te$t-password");
        Vector<String> queries = new Vector<String>();
        queries.add("telcfg:settings/SIP/count");
        try {
            fbc.login();
            if (QueryType.UNKNOWN == fbc.getQueryMethodForFritzBox().getQueryType()) {
                fail("Could not detect query type!");
            };

            // test query
            Vector<String> response = fbc.getQuery(queries);
            Assert.assertNotNull(response);
        } catch (LoginBlockedException e) {
            fail("Login is blocked. Check your credentials!");
        } catch (InvalidCredentialsException e) {
            fail("Login failed. Check your credentials!");
        } catch (InvalidSessionIdException e) {
            fail("Invalid sessionId detected");
        }

        Assert.assertNotNull(loginMethod);
        Assert.assertNotNull(loginMode);
    }
}
