package org.jfritz.fboxlib.internal.login;

import junit.framework.Assert;
import org.jfritz.fboxlib.enums.LoginMethod;
import org.jfritz.fboxlib.enums.LoginMode;
import org.jfritz.fboxlib.fritzbox.FirmwareVersion;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

public class FritzLoginTest {

    private final int boxType = 0;
    private FritzLogin login;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        login = new FritzLogin();
    }

    @Test
    public void fw_0_0() {
        byte major = 0;
        byte minor = 0;
        FirmwareVersion fw = new FirmwareVersion(boxType, major, minor);
        Assert.assertEquals(LoginMethod.PlainPassword, login.getLoginMethodByFirmware(fw));
    }

    @Test
    public void fw_03_00() {
        byte major = 3;
        byte minor = 0;
        FirmwareVersion fw = new FirmwareVersion(boxType, major, minor);
        Assert.assertEquals(LoginMethod.PlainPassword, login.getLoginMethodByFirmware(fw));
    }

    @Test
    public void fw_4_15() {
        byte major = 4;
        byte minor = 15;
        FirmwareVersion fw = new FirmwareVersion(boxType, major, minor);
        Assert.assertEquals(LoginMethod.PlainPassword, login.getLoginMethodByFirmware(fw));
    }

    @Test
    public void fw_4_73() {
        byte major = 4;
        byte minor = 73;
        FirmwareVersion fw = new FirmwareVersion(boxType, major, minor);
        Assert.assertEquals(LoginMethod.PlainPassword, login.getLoginMethodByFirmware(fw));
    }

    @Test
    public void fw_4_7$() {
        byte major = 4;
        byte minor = 74;
        FirmwareVersion fw = new FirmwareVersion(boxType, major, minor);
        Assert.assertEquals(LoginMethod.SidXml, login.getLoginMethodByFirmware(fw));
    }

    @Test
    public void fw_4_75() {
        byte major = 4;
        byte minor = 75;
        FirmwareVersion fw = new FirmwareVersion(boxType, major, minor);
        Assert.assertEquals(LoginMethod.SidXml, login.getLoginMethodByFirmware(fw));
    }

    @Test
    public void fw_5_5() {
        byte major = 5;
        byte minor = 5;
        FirmwareVersion fw = new FirmwareVersion(boxType, major, minor);
        Assert.assertEquals(LoginMethod.SidXml, login.getLoginMethodByFirmware(fw));
    }

    @Test
    public void fw_5_49() {
        byte major = 5;
        byte minor = 49;
        FirmwareVersion fw = new FirmwareVersion(boxType, major, minor);
        Assert.assertEquals(LoginMethod.SidXml, login.getLoginMethodByFirmware(fw));
    }

    @Test
    public void fw_5_50() {
        byte major = 5;
        byte minor = 50;
        FirmwareVersion fw = new FirmwareVersion(boxType, major, minor);
        Assert.assertEquals(LoginMethod.SidLua, login.getLoginMethodByFirmware(fw));
    }

    @Test
    public void fw_5_53() {
        byte major = 5;
        byte minor = 53;
        FirmwareVersion fw = new FirmwareVersion(boxType, major, minor);
        Assert.assertEquals(LoginMethod.SidLua, login.getLoginMethodByFirmware(fw));
    }

    @Test
    public void fw_5_59() {
        byte major = 5;
        byte minor = 59;
        FirmwareVersion fw = new FirmwareVersion(boxType, major, minor);
        Assert.assertEquals(LoginMethod.SidLua, login.getLoginMethodByFirmware(fw));
    }

    @Test
    public void fw_5_60() {
        byte major = 5;
        byte minor = 60;
        FirmwareVersion fw = new FirmwareVersion(boxType, major, minor);
        Assert.assertEquals(LoginMethod.SidLua, login.getLoginMethodByFirmware(fw));
    }

    @Test
    public void fw_6_0() {
        byte major = 6;
        byte minor = 0;
        FirmwareVersion fw = new FirmwareVersion(boxType, major, minor);
        Assert.assertEquals(LoginMethod.SidLua, login.getLoginMethodByFirmware(fw));
    }

    @Test
    public void comp_mode_0___fw_06_24() {
        String line = "  [\"boxusers:settings/compatibility_mode\"] = \"0\",";
        login.fwVersion = new FirmwareVersion((byte) 113, (byte) 06, (byte) 24);
        login.extractLoginMode(line);
        LoginMode mode = login.getLoginMode();

        Assert.assertEquals(LoginMode.USERNAME_PASSWORD, mode);
    }

    @Test
    public void comp_mode_1___fw_06_24() {
        String line = "  [\"boxusers:settings/compatibility_mode\"] = \"1\",";
        login.fwVersion = new FirmwareVersion((byte) 113, (byte) 06, (byte) 24);
        login.extractLoginMode(line);
        LoginMode mode = login.getLoginMode();

        Assert.assertEquals(LoginMode.PASSWORD, mode);
    }

    @Test
    public void comp_mode_non_numeric___fw_06_24() {
        String line = "  [\"boxusers:settings/compatibility_mode\"] = \"abc\",";
        login.fwVersion = new FirmwareVersion((byte) 113, (byte) 06, (byte) 24);
        login.extractLoginMode(line);
        LoginMode mode = login.getLoginMode();

        Assert.assertEquals(LoginMode.PASSWORD, mode);
    }

    @Test
    public void comp_mode_empty___fw_06_24() {
        String line = "";
        login.fwVersion = new FirmwareVersion((byte) 113, (byte) 06, (byte) 24);
        login.extractLoginMode(line);
        LoginMode mode = login.getLoginMode();

        Assert.assertEquals(LoginMode.PASSWORD, mode);
    }

    @Test
    public void comp_mode_null___fw_06_24() {
        String line = null;
        login.fwVersion = new FirmwareVersion((byte) 113, (byte) 06, (byte) 24);
        login.extractLoginMode(line);
        LoginMode mode = login.getLoginMode();

        Assert.assertEquals(LoginMode.PASSWORD, mode);
    }

    @Test
    public void lastUserName___fw_06_24() {
        String line = "  [\"boxusers:settings/last_homenetwork_username\"] = \"robotniko\",";
        login.fwVersion = new FirmwareVersion((byte) 113, (byte) 06, (byte) 24);
        login.extractLastUsername(line);
        String userName = login.getLastUserName();

        Assert.assertEquals("robotniko", userName);
    }

    @Test
    public void lastUserNameEmpty___fw_06_24() {
        String line = "  [\"boxusers:settings/last_homenetwork_username\"] = \"\",";
        login.fwVersion = new FirmwareVersion((byte) 113, (byte) 06, (byte) 24);
        login.extractLastUsername(line);
        String userName = login.getLastUserName();

        Assert.assertEquals("", userName);
    }

    @Test
    public void lastUserNameNull___fw_06_24() {
        String line = null;
        login.fwVersion = new FirmwareVersion((byte) 113, (byte) 06, (byte) 24);
        login.extractLastUsername(line);
        String userName = login.getLastUserName();

        Assert.assertEquals("", userName);
    }

    @Test
    public void comp_mode_0___fw_06_25() {
        String line = "  <select id=\"uiViewUser\" name=\"uiUser\" tabindex=\"1\">";
        login.fwVersion = new FirmwareVersion((byte) 113, (byte) 06, (byte) 25);
        login.extractLoginMode(line);
        LoginMode mode = login.getLoginMode();

        Assert.assertEquals(LoginMode.USERNAME_PASSWORD, mode);
    }

    @Test
    public void comp_mode_1___fw_06_25() {
        String line = "  <select id=\"uiViewUser\" name=\"uiUser\" tabindex=\"0\">";
        login.fwVersion = new FirmwareVersion((byte) 113, (byte) 06, (byte) 25);
        login.extractLoginMode(line);
        LoginMode mode = login.getLoginMode();

        Assert.assertEquals(LoginMode.PASSWORD, mode);
    }

    @Test
    public void comp_mode_empty___fw_06_25() {
        String line = "";
        login.fwVersion = new FirmwareVersion((byte) 113, (byte) 06, (byte) 25);
        login.extractLoginMode(line);
        LoginMode mode = login.getLoginMode();

        Assert.assertEquals(LoginMode.PASSWORD, mode);
    }

    @Test
    public void comp_mode_null___fw_06_25() {
        String line = null;
        login.fwVersion = new FirmwareVersion((byte) 113, (byte) 06, (byte) 25);
        login.extractLoginMode(line);
        LoginMode mode = login.getLoginMode();

        Assert.assertEquals(LoginMode.PASSWORD, mode);
    }

    @Test
    public void lastUserName___fw_06_25() {
        String line = " <input type=\"hidden\" id=\"username\" name=\"username\" value=\"robotniko\"> ";
        login.fwVersion = new FirmwareVersion((byte) 113, (byte) 06, (byte) 25);
        login.extractLastUsername(line);
        String userName = login.getLastUserName();

        Assert.assertEquals("robotniko", userName);
    }

    @Test
    public void lastUserNameEmpty___fw_06_25() {
        String line = " <input type=\"hidden\" id=\"username\" name=\"username\" value=\"\"> ";
        login.fwVersion = new FirmwareVersion((byte) 113, (byte) 06, (byte) 25);
        login.extractLastUsername(line);
        String userName = login.getLastUserName();

        Assert.assertEquals("", userName);
    }

    @Test
    public void lastUserNameNull___fw_06_25() {
        String line = null;
        login.fwVersion = new FirmwareVersion((byte) 113, (byte) 06, (byte) 25);
        login.extractLastUsername(line);
        String userName = login.getLastUserName();

        Assert.assertEquals("", userName);
    }

    @Test
    public void comp_mode_0___fw_06_35() {
        String line = " \"showUser\": false,";
        login.fwVersion = new FirmwareVersion((byte) 113, (byte) 06, (byte) 35);
        login.extractLoginMode(line);
        LoginMode mode = login.getLoginMode();

        Assert.assertEquals(LoginMode.PASSWORD, mode);
    }

    @Test
    public void comp_mode_1___fw_06_35() {
        String line = " \"showUser\": true,";
        login.fwVersion = new FirmwareVersion((byte) 113, (byte) 06, (byte) 35);
        login.extractLoginMode(line);
        LoginMode mode = login.getLoginMode();

        Assert.assertEquals(LoginMode.USERNAME_PASSWORD, mode);
    }

    @Test
    public void comp_mode_empty___fw_06_35() {
        String line = "";
        login.fwVersion = new FirmwareVersion((byte) 113, (byte) 06, (byte) 35);
        login.extractLoginMode(line);
        LoginMode mode = login.getLoginMode();

        Assert.assertEquals(LoginMode.PASSWORD, mode);
    }

    @Test
    public void comp_mode_null___fw_06_35() {
        String line = null;
        login.fwVersion = new FirmwareVersion((byte) 113, (byte) 06, (byte) 35);
        login.extractLoginMode(line);
        LoginMode mode = login.getLoginMode();

        Assert.assertEquals(LoginMode.PASSWORD, mode);
    }

    @Test
    public void lastUserName___fw_06_35() {
        String line = " \"username\": \"robotniko\", ";
        login.fwVersion = new FirmwareVersion((byte) 113, (byte) 06, (byte) 35);
        login.extractLastUsername(line);
        String userName = login.getLastUserName();

        Assert.assertEquals("robotniko", userName);
    }

    @Test
    public void lastUserNameEmpty___fw_06_35() {
        String line = " <input type=\"hidden\" id=\"username\" name=\"username\" value=\"\"> ";
        login.fwVersion = new FirmwareVersion((byte) 113, (byte) 06, (byte) 35);
        login.extractLastUsername(line);
        String userName = login.getLastUserName();

        Assert.assertEquals("", userName);
    }

    @Test
    public void lastUserNameNull___fw_06_35() {
        String line = null;
        login.fwVersion = new FirmwareVersion((byte) 113, (byte) 06, (byte) 35);
        login.extractLastUsername(line);
        String userName = login.getLastUserName();

        Assert.assertEquals("", userName);
    }
}
