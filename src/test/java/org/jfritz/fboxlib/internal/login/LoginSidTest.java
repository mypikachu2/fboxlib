package org.jfritz.fboxlib.internal.login;

import junit.framework.Assert;
import org.jfritz.fboxlib.fritzbox.FritzBoxCommunication;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class LoginSidTest {

    @Mock
    private FritzBoxCommunication mockedFbc;
    private LoginSid loginSid;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        loginSid = new LoginSid(mockedFbc);
    }

    @Test
    public void challengeWithSpecialChars() {
        String challengeResponse = loginSid.generateResponseFromChallengeAndPassword("1234567z", "äbc");
        Assert.assertEquals("1234567z-9e224a41eeefa284df7bb0f26c2913e2", challengeResponse);
    }

    @Test
    public void challengeWithUTF16Chars() {
        String challengeResponse = loginSid.generateResponseFromChallengeAndPassword("1234567z", "Ābc");
        Assert.assertEquals("1234567z-4d422a0edeeded87635c6de7ff5857e2", challengeResponse);
    }
}
