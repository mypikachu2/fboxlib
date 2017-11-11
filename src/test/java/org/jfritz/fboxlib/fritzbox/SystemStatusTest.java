package org.jfritz.fboxlib.fritzbox;

import junit.framework.Assert;
import org.jfritz.fboxlib.exceptions.FirmwareNotDetectedException;
import org.jfritz.fboxlib.exceptions.PageNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import static org.mockito.Mockito.doReturn;

public class SystemStatusTest {

    @Mock
    private FritzBoxCommunication mockedFbc;
    private SystemStatus systemStatus;
    private FirmwareVersion detectFirmware;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        systemStatus = new SystemStatus();
        detectFirmware = new FirmwareVersion();
    }

    @Test(expected = FirmwareNotDetectedException.class)
    public void responseNull() throws FirmwareNotDetectedException {
        String response = null;
        systemStatus.parseResponse(response);
        detectFirmware = systemStatus.getFirmwareVersion();
    }

    @Test(expected = FirmwareNotDetectedException.class)
    public void responseEmpty() throws FirmwareNotDetectedException {
        String response = "";
        systemStatus.parseResponse(response);
        detectFirmware = systemStatus.getFirmwareVersion();
    }

    @Test(expected = FirmwareNotDetectedException.class)
    public void noHyphen() throws FirmwareNotDetectedException {
        String response = "abcdef";
        systemStatus.parseResponse(response);
        detectFirmware = systemStatus.getFirmwareVersion();
    }

    @Test(expected = FirmwareNotDetectedException.class)
    public void tooShort() throws FirmwareNotDetectedException {
        String response = "abc-def";
        systemStatus.parseResponse(response);
        detectFirmware = systemStatus.getFirmwareVersion();
    }

    @Test(expected = FirmwareNotDetectedException.class)
    public void tooFirmwareTooShort() throws FirmwareNotDetectedException {
        String response = "1-2-3-4-5-6-7-8-9-0-1-2";
        systemStatus.parseResponse(response);
        detectFirmware = systemStatus.getFirmwareVersion();
    }

    @Test
    public void test_7390_840522_22574_1und1_de() throws FirmwareNotDetectedException {
        String response = wrapInHtml("FRITZ!Box Fon WLAN 7390 (UI)-B-000103-020314-055724-201216-217902-840522-22574-1und1");
        systemStatus.parseResponse(response);
        detectFirmware = systemStatus.getFirmwareVersion();

        Assert.assertEquals("FRITZ!Box Fon WLAN 7390 (UI)", detectFirmware.getName());
        Assert.assertEquals("B", detectFirmware.getAnnex());
        Assert.assertEquals(84, detectFirmware.getBoxType());
        Assert.assertEquals(5, detectFirmware.getMajor());
        Assert.assertEquals(22, detectFirmware.getMinor());
        Assert.assertEquals(22574, detectFirmware.getRevision());
        Assert.assertEquals("1und1", detectFirmware.getBranding());
        Assert.assertEquals("de", detectFirmware.getLanguage());
    }

    private String wrapInHtml(String input) {
        return "<html><body>" + input + "</body></html>";
    }

    @Test
    public void test_7390_840488_18808_avm_de() throws FirmwareNotDetectedException {
        String response = wrapInHtml("FRITZ!Box Fon WLAN 7390-B-190210-010106-630046-320710-787902-840488-18808-avm");
        systemStatus.parseResponse(response);
        detectFirmware = systemStatus.getFirmwareVersion();

        Assert.assertEquals("FRITZ!Box Fon WLAN 7390", detectFirmware.getName());
        Assert.assertEquals("B", detectFirmware.getAnnex());
        Assert.assertEquals(84, detectFirmware.getBoxType());
        Assert.assertEquals(4, detectFirmware.getMajor());
        Assert.assertEquals(88, detectFirmware.getMinor());
        Assert.assertEquals(18808, detectFirmware.getRevision());
        Assert.assertEquals("avm", detectFirmware.getBranding());
        Assert.assertEquals("de", detectFirmware.getLanguage());
    }

    @Test
    public void test_7270_740522_22574_1und1_de() throws FirmwareNotDetectedException {
        String response = wrapInHtml("FRITZ!Box Fon WLAN 7270 v3 (UI)-B-220107-020425-604741-073467-787902-740522-22574-1und1");
        systemStatus.parseResponse(response);
        detectFirmware = systemStatus.getFirmwareVersion();

        Assert.assertEquals("FRITZ!Box Fon WLAN 7270 v3 (UI)", detectFirmware.getName());
        Assert.assertEquals("B", detectFirmware.getAnnex());
        Assert.assertEquals(74, detectFirmware.getBoxType());
        Assert.assertEquals(5, detectFirmware.getMajor());
        Assert.assertEquals(22, detectFirmware.getMinor());
        Assert.assertEquals(22574, detectFirmware.getRevision());
        Assert.assertEquals("1und1", detectFirmware.getBranding());
        Assert.assertEquals("de", detectFirmware.getLanguage());
    }

    @Test
    public void test_7330_1160522_22574_1und1_de() throws FirmwareNotDetectedException {
        String response = wrapInHtml("FRITZ!Box 7330 SL (UI)-B-052405-000007-250637-757236-787902-1160522-22574-1und1");
        systemStatus.parseResponse(response);
        detectFirmware = systemStatus.getFirmwareVersion();

        Assert.assertEquals("FRITZ!Box 7330 SL (UI)", detectFirmware.getName());
        Assert.assertEquals("B", detectFirmware.getAnnex());
        Assert.assertEquals(116, detectFirmware.getBoxType());
        Assert.assertEquals(5, detectFirmware.getMajor());
        Assert.assertEquals(22, detectFirmware.getMinor());
        Assert.assertEquals(22574, detectFirmware.getRevision());
        Assert.assertEquals("1und1", detectFirmware.getBranding());
        Assert.assertEquals("de", detectFirmware.getLanguage());
    }

    @Test
    public void test_WLAN_080449_10836_avne_en() throws FirmwareNotDetectedException {
        String response = wrapInHtml("FRITZ!Box Fon WLAN-B-111500-000104-303630-437613-787902-080449-10836-avme-en");
        systemStatus.parseResponse(response);
        detectFirmware = systemStatus.getFirmwareVersion();

        Assert.assertEquals("FRITZ!Box Fon WLAN", detectFirmware.getName());
        Assert.assertEquals("B", detectFirmware.getAnnex());
        Assert.assertEquals(8, detectFirmware.getBoxType());
        Assert.assertEquals(4, detectFirmware.getMajor());
        Assert.assertEquals(49, detectFirmware.getMinor());
        Assert.assertEquals(10836, detectFirmware.getRevision());
        Assert.assertEquals("avme", detectFirmware.getBranding());
        Assert.assertEquals("en", detectFirmware.getLanguage());
    }

    @Test
    public void test_6360_850525_22677_kabelbw_de() throws FirmwareNotDetectedException {
        String response = wrapInHtml("FRITZ!Box 6360 Cable (kbw)-Kabel-212806-010307-577727-370307-787902-850525-22677-kabelbw");
        systemStatus.parseResponse(response);
        detectFirmware = systemStatus.getFirmwareVersion();

        Assert.assertEquals("FRITZ!Box 6360 Cable (kbw)", detectFirmware.getName());
        Assert.assertEquals("Kabel", detectFirmware.getAnnex());
        Assert.assertEquals(85, detectFirmware.getBoxType());
        Assert.assertEquals(5, detectFirmware.getMajor());
        Assert.assertEquals(25, detectFirmware.getMinor());
        Assert.assertEquals(22677, detectFirmware.getRevision());
        Assert.assertEquals("kabelbw", detectFirmware.getBranding());
        Assert.assertEquals("de", detectFirmware.getLanguage());
    }

    @Test
    public void test_7570_750491_19965_avme_de() throws FirmwareNotDetectedException {
        String response = wrapInHtml("FRITZ!Box Fon WLAN 7570 vDSL-B-000000-000000-000000-000000-000000-750491-19965-avme-de");
        systemStatus.parseResponse(response);
        detectFirmware = systemStatus.getFirmwareVersion();

        Assert.assertEquals("FRITZ!Box Fon WLAN 7570 vDSL", detectFirmware.getName());
        Assert.assertEquals("B", detectFirmware.getAnnex());
        Assert.assertEquals(75, detectFirmware.getBoxType());
        Assert.assertEquals(4, detectFirmware.getMajor());
        Assert.assertEquals(91, detectFirmware.getMinor());
        Assert.assertEquals(19965, detectFirmware.getRevision());
        Assert.assertEquals("avme", detectFirmware.getBranding());
        Assert.assertEquals("de", detectFirmware.getLanguage());
    }

    @Test
    public void test_7312_1170523_22847_1und1_de() throws FirmwareNotDetectedException {
        String response = wrapInHtml("FRITZ!Box 7312 (UI)-B-071403-000017-100563-156351-787902-1170523-22847-1und1");
        systemStatus.parseResponse(response);
        detectFirmware = systemStatus.getFirmwareVersion();

        Assert.assertEquals("FRITZ!Box 7312 (UI)", detectFirmware.getName());
        Assert.assertEquals("B", detectFirmware.getAnnex());
        Assert.assertEquals(117, detectFirmware.getBoxType());
        Assert.assertEquals(5, detectFirmware.getMajor());
        Assert.assertEquals(23, detectFirmware.getMinor());
        Assert.assertEquals(22847, detectFirmware.getRevision());
        Assert.assertEquals("1und1", detectFirmware.getBranding());
        Assert.assertEquals("de", detectFirmware.getLanguage());
    }

    @Test
    public void test_6360_850528_23625_unity_de() throws FirmwareNotDetectedException {
        String response = wrapInHtml("FRITZ!Box 6360 Cable (um)-Kabel-211600-000010-054167-341625-787902-850528-23625-unity");
        systemStatus.parseResponse(response);
        detectFirmware = systemStatus.getFirmwareVersion();

        Assert.assertEquals("FRITZ!Box 6360 Cable (um)", detectFirmware.getName());
        Assert.assertEquals("Kabel", detectFirmware.getAnnex());
        Assert.assertEquals(85, detectFirmware.getBoxType());
        Assert.assertEquals(5, detectFirmware.getMajor());
        Assert.assertEquals(28, detectFirmware.getMinor());
        Assert.assertEquals(23625, detectFirmware.getRevision());
        Assert.assertEquals("unity", detectFirmware.getBranding());
        Assert.assertEquals("de", detectFirmware.getLanguage());
    }

    @Test
    public void testaaa() throws FirmwareNotDetectedException {
        String response = wrapInHtml("<html><body>FRITZ!Box 7362 SL (UI)-B-090500-000005-240750-414002-787902-1310560-26970-1und1</body></html>");
        systemStatus.parseResponse(response);
        detectFirmware = systemStatus.getFirmwareVersion();

        Assert.assertEquals("FRITZ!Box 7362 SL (UI)", detectFirmware.getName());
        Assert.assertEquals("B", detectFirmware.getAnnex());
        Assert.assertEquals(131, detectFirmware.getBoxType());
        Assert.assertEquals(5, detectFirmware.getMajor());
        Assert.assertEquals(60, detectFirmware.getMinor());
        Assert.assertEquals(26970, detectFirmware.getRevision());
        Assert.assertEquals("1und1", detectFirmware.getBranding());
        Assert.assertEquals("de", detectFirmware.getLanguage());
    }

    @Test
    public void testOverwrite() throws FirmwareNotDetectedException {
        String response = wrapInHtml("<html><body>FRITZ!Box Fon WLAN 7390-B-010702-000024-006117-745743-147902-overwrite feature CONFIG_WLAN_HOTSPOT=y-840623-overwrite feature CONFIG_WLAN_HOTSPOT=y-29836-avm</body></html>");
        systemStatus.parseResponse(response);
        detectFirmware = systemStatus.getFirmwareVersion();

        Assert.assertEquals("FRITZ!Box Fon WLAN 7390", detectFirmware.getName());
        Assert.assertEquals("B", detectFirmware.getAnnex());
        Assert.assertEquals(84, detectFirmware.getBoxType());
        Assert.assertEquals(6, detectFirmware.getMajor());
        Assert.assertEquals(23, detectFirmware.getMinor());
        Assert.assertEquals(29836, detectFirmware.getRevision());
        Assert.assertEquals("avm", detectFirmware.getBranding());
        Assert.assertEquals("de", detectFirmware.getLanguage());
    }

    @Test
    public void testFreetz() throws FirmwareNotDetectedException {
        String response = wrapInHtml("<html><body>FRITZ!Box Fon WLAN 7390-B-073009-000803-027244-236766-147902-840600-26762-avm<br>\nFRITZ!Box Modifikation <subversion>, <a href=\"http://www.freetz.org\" target=\"_blank\">http://www.freetz.org</a>\n --- Optionen: <options>\n<br>\n</body></html>");
        systemStatus.parseResponse(response);
        detectFirmware = systemStatus.getFirmwareVersion();

        Assert.assertEquals("FRITZ!Box Fon WLAN 7390", detectFirmware.getName());
        Assert.assertEquals("B", detectFirmware.getAnnex());
        Assert.assertEquals(84, detectFirmware.getBoxType());
        Assert.assertEquals(6, detectFirmware.getMajor());
        Assert.assertEquals(00, detectFirmware.getMinor());
        Assert.assertEquals(26762, detectFirmware.getRevision());
        Assert.assertEquals("avm", detectFirmware.getBranding());
        Assert.assertEquals("de", detectFirmware.getLanguage());
    }

    @Test
    public void testSpeedToFritz_1() throws FirmwareNotDetectedException {
        String response = wrapInHtml("<html><body>FRITZ!Box Fon WLAN 7270 v2-B-210201-040120-412214-603656-216702-540490-19715-avme-deFRITZ!Box Speed-to-fritz Modifikation Skript Datum und Revisionsnummer: 11.07.10 --- Optionen: Siehe /etc/Firmware.conf und Modinfo on Statusseite.</body></html>");
        systemStatus.parseResponse(response);
        detectFirmware = systemStatus.getFirmwareVersion();

        Assert.assertEquals("FRITZ!Box Fon WLAN 7270 v2", detectFirmware.getName());
        Assert.assertEquals("B", detectFirmware.getAnnex());
        Assert.assertEquals(54, detectFirmware.getBoxType());
        Assert.assertEquals(4, detectFirmware.getMajor());
        Assert.assertEquals(90, detectFirmware.getMinor());
        Assert.assertEquals(19715, detectFirmware.getRevision());
        Assert.assertEquals("avme", detectFirmware.getBranding());
        Assert.assertEquals("de", detectFirmware.getLanguage());
    }

    @Test
    public void testSpeedToFritz_2() throws FirmwareNotDetectedException {
        String response = wrapInHtml("<html><body>FRITZ!Box Fon WLAN 7270 v2-B-210201-040120-412214-603656-216702-540490-19715-avme-de</body></html>");
        systemStatus.parseResponse(response);
        detectFirmware = systemStatus.getFirmwareVersion();

        Assert.assertEquals("FRITZ!Box Fon WLAN 7270 v2", detectFirmware.getName());
        Assert.assertEquals("B", detectFirmware.getAnnex());
        Assert.assertEquals(54, detectFirmware.getBoxType());
        Assert.assertEquals(04, detectFirmware.getMajor());
        Assert.assertEquals(90, detectFirmware.getMinor());
        Assert.assertEquals(19715, detectFirmware.getRevision());
        Assert.assertEquals("avme", detectFirmware.getBranding());
        Assert.assertEquals("de", detectFirmware.getLanguage());
    }

//	FRITZ!Box Fon WLAN 7050 (UI)-B-182303-030229-064572-510141-787902-140433-7238-1und1
//	FRITZ!Box Fon WLAN 7390-B-151308-000125-223256-726042-147902-840522-22574-avm
//	FRITZ!Box Fon WLAN 7270 v3-B-191408-010214-273031-462044-197902-740522-22574-avm
//	FRITZ!Box Fon WLAN 7240-B-212107-000104-725364-203310-147902-730522-22574-avm
//	FRITZ!Box Fon WLAN 7390-B-192204-020417-266755-643405-787902-840527-23565-avm
//	FRITZ!Box Fon WLAN-B-111500-000104-303630-437613-787902-080449-10836-avme-en
//	FRITZ!Box Fon WLAN 7270 v2-B-041411-000314-775335-655674-197902-540529-24296-avm
//	FRITZ!Box Fon WLAN 7390-B-110506-000023-243373-563631-787902-840550-24230-avm
//	FRITZ!Box Fon WLAN 7270 v1-B-150601-020207-652064-206152-787902-540488-18902-avm
//	FRITZ!Box Fon WLAN 7270-B-182409-000317-436757-744335-217902-540480-16540-avm
//	FRITZ!Box Fon WLAN 7170-B-092007-040628-457563-147110-217902-290487-19985-avm
//	FRITZ!Box Fon WLAN 7170-B-232101-010617-457563-147110-217902-290487-19985-avm
//	FRITZ!Box Fon WLAN 7170-B-202304-030824-321264-210372-217902-290480-16352-avm
//	FRITZ!Box Fon WLAN 7170-B-150105-010603-006420-020317-217902-290482-17260-avme-en
//	FRITZ!Box Fon WLAN 7390-B-041610-000311-154106-335352-217902-840522-22574-avm
//	FRITZ!Box Fon WLAN 7113-B-030909-010209-103166-333136-217902-600467-13639-avm
//	FRITZ!Box Fon-B-021200-000100-322100-005115-217902-060433-7703-avm
//	FRITZ!Fon 7150-B-031203-010229-577226-323706-217902-380471-14616-avm
//	FRITZ!Box 6360 Cable (kbw)-Kabel-212806-010307-577727-370307-787902-850525-22677-kabelbw
//	FRITZ!Box Fon WLAN-B-151002-030405-204524-232217-836702-080434-7804-avm
//	FRITZ!Box Fon WLAN 7240-B-101810-020821-513752-526014-146702-730529-24234-avm
//	FRITZ!Box Fon WLAN 7270 v3 (UI)-B-222908-020429-604741-073467-787902-740522-22574-1und1
//	FRITZ!Box Fon WLAN 7390 (UI)-B-031110-000306-137077-747027-787902-840550-24230-1und1

// overwrite-Problem:
//	FRITZ!Box Fon WLAN 7390-B-010702-000024-006117-745743-147902-overwrite feature CONFIG_WLAN_HOTSPOT=y-840623-overwrite feature CONFIG_WLAN_HOTSPOT=y-29836-avm

// Freetz Problem:
//	FRITZ!Box Fon WLAN 7390-B-073009-000803-027244-236766-147902-840600-26762-avm<br>\nFRITZ!Box Modifikation <subversion>, <a href=\"http://www.freetz.org\" target=\"_blank\">http://www.freetz.org</a>\n --- Optionen: <options>\n<br>\n

// Speed-to-fritz Problem
//	FRITZ!Box Fon WLAN 7270 v2-B-210201-040120-412214-603656-216702-540490-19715-avme-deFRITZ!Box Speed-to-fritz Modifikation Skript Datum und Revisionsnummer: 11.07.10 --- Optionen: Siehe /etc/Firmware.conf und Modinfo on Statusseite.
//	FRITZ!Box Fon WLAN 7270 v2-B-210201-040120-412214-603656-216702-540490-19715-avme-de

    @Test
    public void test() throws FirmwareNotDetectedException, IOException, PageNotFoundException {
        doReturn("<html><body>FRITZ!Box Fon WLAN 7390 (UI)-B-200103-020314-055724-201216-217902-840522-22574-1und1</body></html>").when(this.mockedFbc).getSystemStatus();

        FirmwareVersion fw = systemStatus.getFWVersionFromSystemStatus(mockedFbc);
        Assert.assertEquals("FRITZ!Box Fon WLAN 7390 (UI)", fw.getName());
        Assert.assertEquals("B", fw.getAnnex());
        Assert.assertEquals(84, fw.getBoxType());
        Assert.assertEquals(5, fw.getMajor());
        Assert.assertEquals(22, fw.getMinor());
        Assert.assertEquals(22574, fw.getRevision());
        Assert.assertEquals("1und1", fw.getBranding());
        Assert.assertEquals("de", fw.getLanguage());
    }

}
