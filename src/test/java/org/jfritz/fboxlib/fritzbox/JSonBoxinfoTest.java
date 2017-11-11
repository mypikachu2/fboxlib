package org.jfritz.fboxlib.fritzbox;

import junit.framework.Assert;
import org.jfritz.fboxlib.exceptions.FirmwareNotDetectedException;
import org.jfritz.fboxlib.exceptions.PageNotFoundException;
import org.json.simple.parser.ParseException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import static org.mockito.Mockito.when;

public class JSonBoxinfoTest {

    @Mock
    private FritzBoxCommunication mockedFbc;
    private JSonBoxinfo jsonBoxinfo;

    @Before
    public void init() throws IOException, FirmwareNotDetectedException, PageNotFoundException {
        MockitoAnnotations.initMocks(this);

    }

    @Test
    public void responseNull() throws IOException, FirmwareNotDetectedException, PageNotFoundException, ParseException {
        when(mockedFbc.getJsonBoxInfo()).thenReturn(null);
        jsonBoxinfo = new JSonBoxinfo(mockedFbc);

        Assert.assertEquals("not_detected", jsonBoxinfo.getName());
        Assert.assertEquals("not_detected", jsonBoxinfo.getHw());
        Assert.assertEquals("not_detected", jsonBoxinfo.getVersion());
        Assert.assertEquals("not_detected", jsonBoxinfo.getRevision());
        Assert.assertEquals("not_detected", jsonBoxinfo.getSerial());
        Assert.assertEquals("not_detected", jsonBoxinfo.getOem());
        Assert.assertEquals("not_detected", jsonBoxinfo.getLang());
        Assert.assertEquals("not_detected", jsonBoxinfo.getAnnex());
        Assert.assertEquals("not_detected", jsonBoxinfo.getLab());
        Assert.assertEquals("not_detected", jsonBoxinfo.getCountry());
        Assert.assertEquals("not_detected", jsonBoxinfo.getFlag());
        Assert.assertEquals("not_detected", jsonBoxinfo.getUpdateConfig());
        Assert.assertEquals("not_detected", jsonBoxinfo.getSerial());
    }

    @Test
    public void responseEmpty() throws IOException, FirmwareNotDetectedException, PageNotFoundException, ParseException {
        when(mockedFbc.getJsonBoxInfo()).thenReturn("");
        jsonBoxinfo = new JSonBoxinfo(mockedFbc);

        Assert.assertEquals("not_detected", jsonBoxinfo.getName());
        Assert.assertEquals("not_detected", jsonBoxinfo.getHw());
        Assert.assertEquals("not_detected", jsonBoxinfo.getVersion());
        Assert.assertEquals("not_detected", jsonBoxinfo.getRevision());
        Assert.assertEquals("not_detected", jsonBoxinfo.getSerial());
        Assert.assertEquals("not_detected", jsonBoxinfo.getOem());
        Assert.assertEquals("not_detected", jsonBoxinfo.getLang());
        Assert.assertEquals("not_detected", jsonBoxinfo.getAnnex());
        Assert.assertEquals("not_detected", jsonBoxinfo.getLab());
        Assert.assertEquals("not_detected", jsonBoxinfo.getCountry());
        Assert.assertEquals("not_detected", jsonBoxinfo.getFlag());
        Assert.assertEquals("not_detected", jsonBoxinfo.getUpdateConfig());
        Assert.assertEquals("not_detected", jsonBoxinfo.getSerial());
    }

    @Test
    public void responseNotXML() throws IOException, PageNotFoundException, FirmwareNotDetectedException {
        when(mockedFbc.getJsonBoxInfo()).thenReturn("getcgivars(): Couldn't read CGI input from STDIN.\n");
        jsonBoxinfo = new JSonBoxinfo(mockedFbc);

        Assert.assertEquals("not_detected", jsonBoxinfo.getName());
        Assert.assertEquals("not_detected", jsonBoxinfo.getHw());
        Assert.assertEquals("not_detected", jsonBoxinfo.getVersion());
        Assert.assertEquals("not_detected", jsonBoxinfo.getRevision());
        Assert.assertEquals("not_detected", jsonBoxinfo.getSerial());
        Assert.assertEquals("not_detected", jsonBoxinfo.getOem());
        Assert.assertEquals("not_detected", jsonBoxinfo.getLang());
        Assert.assertEquals("not_detected", jsonBoxinfo.getAnnex());
        Assert.assertEquals("not_detected", jsonBoxinfo.getLab());
        Assert.assertEquals("not_detected", jsonBoxinfo.getCountry());
        Assert.assertEquals("not_detected", jsonBoxinfo.getFlag());
        Assert.assertEquals("not_detected", jsonBoxinfo.getUpdateConfig());
        Assert.assertEquals("not_detected", jsonBoxinfo.getSerial());
    }

    @Test
    public void test1() throws IOException, FirmwareNotDetectedException, PageNotFoundException, ParseException {
        when(mockedFbc.getJsonBoxInfo()).thenReturn("<j:BoxInfo><j:Name>FRITZ!Box 7490</j:Name><j:HW>185</j:HW><j:Version>113.06.30</j:Version><j:Revision>30889</j:Revision><j:Serial>C88C</j:Serial><j:OEM>avm</j:OEM><j:Lang>de</j:Lang><j:Annex>B</j:Annex><j:Lab/><j:Country>049</j:Country><j:Flag>crashreport</j:Flag><j:UpdateConfig>1</j:UpdateConfig></j:BoxInfo>");
        jsonBoxinfo = new JSonBoxinfo(mockedFbc);

        Assert.assertEquals("FRITZ!Box 7490", jsonBoxinfo.getName());
        Assert.assertEquals("185", jsonBoxinfo.getHw());
        Assert.assertEquals("113.06.30", jsonBoxinfo.getVersion());
        Assert.assertEquals("30889", jsonBoxinfo.getRevision());
        Assert.assertEquals("C88C", jsonBoxinfo.getSerial());
        Assert.assertEquals("avm", jsonBoxinfo.getOem());
        Assert.assertEquals("de", jsonBoxinfo.getLang());
        Assert.assertEquals("B", jsonBoxinfo.getAnnex());
        Assert.assertEquals("", jsonBoxinfo.getLab());
        Assert.assertEquals("049", jsonBoxinfo.getCountry());
        Assert.assertEquals("crashreport", jsonBoxinfo.getFlag());
        Assert.assertEquals("1", jsonBoxinfo.getUpdateConfig());
    }

    @Test
    public void test2() throws IOException, FirmwareNotDetectedException, PageNotFoundException, ParseException {
        when(mockedFbc.getJsonBoxInfo()).thenReturn("	<j:BoxInfo>\n+   <j:Name>FRITZ!Box Fon WLAN 7390 (UI)</j:Name>\n	  <j:HW>156</j:HW>\n	  <j:Version>84.05.22</j:Version>\n	  <j:Revision>22574</j:Revision>\n	  <j:Serial>entfernt</j:Serial>\n	  <j:OEM>1und1</j:OEM>\n	  <j:Lang>de</j:Lang>\n	  <j:Annex>B</j:Annex>\n	  <j:Lab/>\n	  <j:Country>049</j:Country>\n	</j:BoxInfo>");
        jsonBoxinfo = new JSonBoxinfo(mockedFbc);

        Assert.assertEquals("FRITZ!Box Fon WLAN 7390 (UI)", jsonBoxinfo.getName());
        Assert.assertEquals("156", jsonBoxinfo.getHw());
        Assert.assertEquals("84.05.22", jsonBoxinfo.getVersion());
        Assert.assertEquals("22574", jsonBoxinfo.getRevision());
        Assert.assertEquals("entfernt", jsonBoxinfo.getSerial());
        Assert.assertEquals("1und1", jsonBoxinfo.getOem());
        Assert.assertEquals("de", jsonBoxinfo.getLang());
        Assert.assertEquals("B", jsonBoxinfo.getAnnex());
        Assert.assertEquals("", jsonBoxinfo.getLab());
        Assert.assertEquals("049", jsonBoxinfo.getCountry());
        Assert.assertEquals("", jsonBoxinfo.getFlag());
        Assert.assertEquals("", jsonBoxinfo.getUpdateConfig());
    }

    @Test
    public void test3() throws IOException, FirmwareNotDetectedException, PageNotFoundException, ParseException {
        when(mockedFbc.getJsonBoxInfo()).thenReturn("<j:BoxInfo><j:Name>FRITZ!Box Fon WLAN 7390</j:Name><j:HW>156</j:HW><j:Version>84.05.50</j:Version><j:Revision>24230</j:Revision><j:Serial>Killer</j:Serial><j:OEM>avm</j:OEM><j:Lang>de</j:Lang><j:Annex>B</j:Annex><j:Lab/><j:Country>049</j:Country><j:Flag>crashreport</j:Flag></j:BoxInfo>");
        jsonBoxinfo = new JSonBoxinfo(mockedFbc);

        Assert.assertEquals("FRITZ!Box Fon WLAN 7390", jsonBoxinfo.getName());
        Assert.assertEquals("156", jsonBoxinfo.getHw());
        Assert.assertEquals("84.05.50", jsonBoxinfo.getVersion());
        Assert.assertEquals("24230", jsonBoxinfo.getRevision());
        Assert.assertEquals("Killer", jsonBoxinfo.getSerial());
        Assert.assertEquals("avm", jsonBoxinfo.getOem());
        Assert.assertEquals("de", jsonBoxinfo.getLang());
        Assert.assertEquals("B", jsonBoxinfo.getAnnex());
        Assert.assertEquals("", jsonBoxinfo.getLab());
        Assert.assertEquals("049", jsonBoxinfo.getCountry());
        Assert.assertEquals("crashreport", jsonBoxinfo.getFlag());
        Assert.assertEquals("", jsonBoxinfo.getUpdateConfig());
    }

    @Test
    public void test4() throws IOException, FirmwareNotDetectedException, PageNotFoundException, ParseException {
        when(mockedFbc.getJsonBoxInfo()).thenReturn("<j:BoxInfo><j:Name>FRITZ!Box Fon WLAN 7270 v2</j:Name><j:HW>139</j:HW><j:Version>54.05.29-24296</j:Version><j:Revision>24296</j:Revision><j:Serial>xxxxxxxxx</j:Serial><j:OEM>avm</j:OEM><j:Lang>de</j:Lang><j:Annex>B</j:Annex><j:Lab>BETA</j:Lab><j:Country>049</j:Country><j:Flag>crashreport</j:Flag></j:BoxInfo>");
        jsonBoxinfo = new JSonBoxinfo(mockedFbc);

        Assert.assertEquals("FRITZ!Box Fon WLAN 7270 v2", jsonBoxinfo.getName());
        Assert.assertEquals("139", jsonBoxinfo.getHw());
        Assert.assertEquals("54.05.29-24296", jsonBoxinfo.getVersion());
        Assert.assertEquals("24296", jsonBoxinfo.getRevision());
        Assert.assertEquals("xxxxxxxxx", jsonBoxinfo.getSerial());
        Assert.assertEquals("avm", jsonBoxinfo.getOem());
        Assert.assertEquals("de", jsonBoxinfo.getLang());
        Assert.assertEquals("B", jsonBoxinfo.getAnnex());
        Assert.assertEquals("BETA", jsonBoxinfo.getLab());
        Assert.assertEquals("049", jsonBoxinfo.getCountry());
        Assert.assertEquals("crashreport", jsonBoxinfo.getFlag());
        Assert.assertEquals("", jsonBoxinfo.getUpdateConfig());
    }

    @Test
    public void test5() throws IOException, FirmwareNotDetectedException, PageNotFoundException, ParseException {
        when(mockedFbc.getJsonBoxInfo()).thenReturn("<j:BoxInfo><j:Name>FRITZ!Box Fon WLAN 7390</j:Name><j:HW>156</j:HW><j:Version>84.05.50</j:Version><j:Revision>24230</j:Revision><j:Serial>Jipp stand hier :-)</j:Serial><j:OEM>avm</j:OEM><j:Lang>de</j:Lang><j:Annex>B</j:Annex><j:Lab/><j:Country>049</j:Country><j:Flag>crashreport</j:Flag></j:BoxInfo>");
        jsonBoxinfo = new JSonBoxinfo(mockedFbc);

        Assert.assertEquals("FRITZ!Box Fon WLAN 7390", jsonBoxinfo.getName());
        Assert.assertEquals("156", jsonBoxinfo.getHw());
        Assert.assertEquals("84.05.50", jsonBoxinfo.getVersion());
        Assert.assertEquals("24230", jsonBoxinfo.getRevision());
        Assert.assertEquals("Jipp stand hier :-)", jsonBoxinfo.getSerial());
        Assert.assertEquals("avm", jsonBoxinfo.getOem());
        Assert.assertEquals("de", jsonBoxinfo.getLang());
        Assert.assertEquals("B", jsonBoxinfo.getAnnex());
        Assert.assertEquals("", jsonBoxinfo.getLab());
        Assert.assertEquals("049", jsonBoxinfo.getCountry());
        Assert.assertEquals("crashreport", jsonBoxinfo.getFlag());
        Assert.assertEquals("", jsonBoxinfo.getUpdateConfig());
    }

    @Test
    public void test6() throws IOException, FirmwareNotDetectedException, PageNotFoundException, ParseException {
        when(mockedFbc.getJsonBoxInfo()).thenReturn("<j:BoxInfo><j:Name>FRITZ!Box Fon WLAN 7270 v1</j:Name><j:HW>122</j:HW><j:Version>54.04.88</j:Version><j:Revision>18902</j:Revision><j:Serial>xxxx</j:Serial><j:OEM>avm</j:OEM><j:Lang>de</j:Lang><j:Annex>B</j:Annex><j:Lab/><j:Country>049</j:Country><j:Flag>nomini</j:Flag></j:BoxInfo>");
        jsonBoxinfo = new JSonBoxinfo(mockedFbc);

        Assert.assertEquals("FRITZ!Box Fon WLAN 7270 v1", jsonBoxinfo.getName());
        Assert.assertEquals("122", jsonBoxinfo.getHw());
        Assert.assertEquals("54.04.88", jsonBoxinfo.getVersion());
        Assert.assertEquals("18902", jsonBoxinfo.getRevision());
        Assert.assertEquals("xxxx", jsonBoxinfo.getSerial());
        Assert.assertEquals("avm", jsonBoxinfo.getOem());
        Assert.assertEquals("de", jsonBoxinfo.getLang());
        Assert.assertEquals("B", jsonBoxinfo.getAnnex());
        Assert.assertEquals("", jsonBoxinfo.getLab());
        Assert.assertEquals("049", jsonBoxinfo.getCountry());
        Assert.assertEquals("nomini", jsonBoxinfo.getFlag());
        Assert.assertEquals("", jsonBoxinfo.getUpdateConfig());
    }

    @Test
    public void test7() throws IOException, FirmwareNotDetectedException, PageNotFoundException, ParseException {
        when(mockedFbc.getJsonBoxInfo()).thenReturn("<j:BoxInfo><j:Name>FRITZ!Box Fon WLAN 7270</j:Name><j:HW>139</j:HW><j:Version>54.04.80-16540</j:Version><j:Revision>16540</j:Revision><j:Serial>entfernt</j:Serial><j:OEM>avm</j:OEM><j:Lang>de</j:Lang><j:Annex>B</j:Annex><j:Lab>NAS</j:Lab><j:Country>049</j:Country></j:BoxInfo>");
        jsonBoxinfo = new JSonBoxinfo(mockedFbc);

        Assert.assertEquals("FRITZ!Box Fon WLAN 7270", jsonBoxinfo.getName());
        Assert.assertEquals("139", jsonBoxinfo.getHw());
        Assert.assertEquals("54.04.80-16540", jsonBoxinfo.getVersion());
        Assert.assertEquals("16540", jsonBoxinfo.getRevision());
        Assert.assertEquals("entfernt", jsonBoxinfo.getSerial());
        Assert.assertEquals("avm", jsonBoxinfo.getOem());
        Assert.assertEquals("de", jsonBoxinfo.getLang());
        Assert.assertEquals("B", jsonBoxinfo.getAnnex());
        Assert.assertEquals("NAS", jsonBoxinfo.getLab());
        Assert.assertEquals("049", jsonBoxinfo.getCountry());
        Assert.assertEquals("", jsonBoxinfo.getFlag());
        Assert.assertEquals("", jsonBoxinfo.getUpdateConfig());
    }

    @Test
    public void test8() throws IOException, FirmwareNotDetectedException, PageNotFoundException, ParseException {
        when(mockedFbc.getJsonBoxInfo()).thenReturn("<j:BoxInfo><j:Name>FRITZ!Box Fon WLAN 7170</j:Name><j:HW>94</j:HW><j:Version>29.04.87</j:Version><j:Revision>19985</j:Revision><j:Serial>entfernt</j:Serial><j:OEM>avm</j:OEM><j:Lang>de</j:Lang><j:Annex>B</j:Annex><j:Lab/><j:Country>049</j:Country><j:Flag>mini</j:Flag></j:BoxInfo>");
        jsonBoxinfo = new JSonBoxinfo(mockedFbc);

        Assert.assertEquals("FRITZ!Box Fon WLAN 7170", jsonBoxinfo.getName());
        Assert.assertEquals("94", jsonBoxinfo.getHw());
        Assert.assertEquals("29.04.87", jsonBoxinfo.getVersion());
        Assert.assertEquals("19985", jsonBoxinfo.getRevision());
        Assert.assertEquals("entfernt", jsonBoxinfo.getSerial());
        Assert.assertEquals("avm", jsonBoxinfo.getOem());
        Assert.assertEquals("de", jsonBoxinfo.getLang());
        Assert.assertEquals("B", jsonBoxinfo.getAnnex());
        Assert.assertEquals("", jsonBoxinfo.getLab());
        Assert.assertEquals("049", jsonBoxinfo.getCountry());
        Assert.assertEquals("mini", jsonBoxinfo.getFlag());
        Assert.assertEquals("", jsonBoxinfo.getUpdateConfig());
    }

    @Test
    public void test9() throws IOException, FirmwareNotDetectedException, PageNotFoundException, ParseException {
        when(mockedFbc.getJsonBoxInfo()).thenReturn("<j:BoxInfo><j:Name>FRITZ!Box Fon WLAN 7170</j:Name><j:HW>94</j:HW><j:Version>29.04.87</j:Version><j:Revision>19985</j:Revision><j:Serial>entfernt</j:Serial><j:OEM>avm</j:OEM><j:Lang>de</j:Lang><j:Annex>B</j:Annex><j:Lab/><j:Country>049</j:Country><j:Flag>nomini</j:Flag></j:BoxInfo>");
        jsonBoxinfo = new JSonBoxinfo(mockedFbc);

        Assert.assertEquals("FRITZ!Box Fon WLAN 7170", jsonBoxinfo.getName());
        Assert.assertEquals("94", jsonBoxinfo.getHw());
        Assert.assertEquals("29.04.87", jsonBoxinfo.getVersion());
        Assert.assertEquals("19985", jsonBoxinfo.getRevision());
        Assert.assertEquals("entfernt", jsonBoxinfo.getSerial());
        Assert.assertEquals("avm", jsonBoxinfo.getOem());
        Assert.assertEquals("de", jsonBoxinfo.getLang());
        Assert.assertEquals("B", jsonBoxinfo.getAnnex());
        Assert.assertEquals("", jsonBoxinfo.getLab());
        Assert.assertEquals("049", jsonBoxinfo.getCountry());
        Assert.assertEquals("nomini", jsonBoxinfo.getFlag());
        Assert.assertEquals("", jsonBoxinfo.getUpdateConfig());
    }

    @Test
    public void test10() throws IOException, FirmwareNotDetectedException, PageNotFoundException, ParseException {
        when(mockedFbc.getJsonBoxInfo()).thenReturn("<j:BoxInfo><j:Name>FRITZ!Box Fon WLAN 7170</j:Name><j:HW>94</j:HW><j:Version>29.04.82</j:Version><j:Revision>17260</j:Revision><j:Serial>entfernt</j:Serial><j:OEM>avme</j:OEM><j:Lang>en</j:Lang><j:Annex>B</j:Annex><j:Lab/><j:Country>044</j:Country></j:BoxInfo>");
        jsonBoxinfo = new JSonBoxinfo(mockedFbc);

        Assert.assertEquals("FRITZ!Box Fon WLAN 7170", jsonBoxinfo.getName());
        Assert.assertEquals("94", jsonBoxinfo.getHw());
        Assert.assertEquals("29.04.82", jsonBoxinfo.getVersion());
        Assert.assertEquals("17260", jsonBoxinfo.getRevision());
        Assert.assertEquals("entfernt", jsonBoxinfo.getSerial());
        Assert.assertEquals("avme", jsonBoxinfo.getOem());
        Assert.assertEquals("en", jsonBoxinfo.getLang());
        Assert.assertEquals("B", jsonBoxinfo.getAnnex());
        Assert.assertEquals("", jsonBoxinfo.getLab());
        Assert.assertEquals("044", jsonBoxinfo.getCountry());
        Assert.assertEquals("", jsonBoxinfo.getFlag());
        Assert.assertEquals("", jsonBoxinfo.getUpdateConfig());
    }

    @Test
    public void test11() throws IOException, FirmwareNotDetectedException, PageNotFoundException, ParseException {
        when(mockedFbc.getJsonBoxInfo()).thenReturn("<j:BoxInfo><j:Name>FRITZ!Box 6360 Cable (kbw)</j:Name><j:HW>157</j:HW><j:Version>85.05.25</j:Version><j:Revision>22677</j:Revision><j:Serial>xxxx</j:Serial><j:OEM>kabelbw</j:OEM><j:Lang>de</j:Lang><j:Annex>Kabel</j:Annex><j:Lab/><j:Country>049</j:Country><j:Flag>crashreport</j:Flag></j:BoxInfo>");
        jsonBoxinfo = new JSonBoxinfo(mockedFbc);

        Assert.assertEquals("FRITZ!Box 6360 Cable (kbw)", jsonBoxinfo.getName());
        Assert.assertEquals("157", jsonBoxinfo.getHw());
        Assert.assertEquals("85.05.25", jsonBoxinfo.getVersion());
        Assert.assertEquals("22677", jsonBoxinfo.getRevision());
        Assert.assertEquals("xxxx", jsonBoxinfo.getSerial());
        Assert.assertEquals("kabelbw", jsonBoxinfo.getOem());
        Assert.assertEquals("de", jsonBoxinfo.getLang());
        Assert.assertEquals("Kabel", jsonBoxinfo.getAnnex());
        Assert.assertEquals("", jsonBoxinfo.getLab());
        Assert.assertEquals("049", jsonBoxinfo.getCountry());
        Assert.assertEquals("crashreport", jsonBoxinfo.getFlag());
        Assert.assertEquals("", jsonBoxinfo.getUpdateConfig());
    }

    @Test
    public void test12() throws IOException, FirmwareNotDetectedException, PageNotFoundException, ParseException {
        when(mockedFbc.getJsonBoxInfo()).thenReturn("<j:BoxInfo xmlns:j=\"http://jason.avm.de/updatecheck/\"><j:Name>FRITZ!Box Fon WLAN 7270 v3 (UI)</j:Name> <j:HW>145</j:HW> <j:Version>74.05.22</j:Version> <j:Revision>22574</j:Revision> <j:Serial>00</j:Serial> <j:OEM>1und1</j:OEM> <j:Lang>de</j:Lang> <j:Annex>B</j:Annex> <j:Lab /> <j:Country>049</j:Country> <j:Flag>crashreport</j:Flag> </j:BoxInfo>");
        jsonBoxinfo = new JSonBoxinfo(mockedFbc);

        Assert.assertEquals("FRITZ!Box Fon WLAN 7270 v3 (UI)", jsonBoxinfo.getName());
        Assert.assertEquals("145", jsonBoxinfo.getHw());
        Assert.assertEquals("74.05.22", jsonBoxinfo.getVersion());
        Assert.assertEquals("22574", jsonBoxinfo.getRevision());
        Assert.assertEquals("00", jsonBoxinfo.getSerial());
        Assert.assertEquals("1und1", jsonBoxinfo.getOem());
        Assert.assertEquals("de", jsonBoxinfo.getLang());
        Assert.assertEquals("B", jsonBoxinfo.getAnnex());
        Assert.assertEquals("", jsonBoxinfo.getLab());
        Assert.assertEquals("049", jsonBoxinfo.getCountry());
        Assert.assertEquals("crashreport", jsonBoxinfo.getFlag());
        Assert.assertEquals("", jsonBoxinfo.getUpdateConfig());
    }

    @Test
    public void test13() throws IOException, FirmwareNotDetectedException, PageNotFoundException, ParseException {
        when(mockedFbc.getJsonBoxInfo()).thenReturn("<j:BoxInfo xmlns:j=\"http://jason.avm.de/updatecheck/\"><j:Name>FRITZ!Box Fon WLAN 7170</j:Name><j:HW>94</j:HW><j:Version>29.04.82</j:Version><j:Revision>17260</j:Revision><j:Serial>1A16</j:Serial><j:OEM>avme</j:OEM><j:Lang>en</j:Lang><j:Annex>B</j:Annex><j:Lab/><j:Country>049</j:Country></j:BoxInfo>");
        jsonBoxinfo = new JSonBoxinfo(mockedFbc);

        Assert.assertEquals("FRITZ!Box Fon WLAN 7170", jsonBoxinfo.getName());
        Assert.assertEquals("94", jsonBoxinfo.getHw());
        Assert.assertEquals("29.04.82", jsonBoxinfo.getVersion());
        Assert.assertEquals("17260", jsonBoxinfo.getRevision());
        Assert.assertEquals("1A16", jsonBoxinfo.getSerial());
        Assert.assertEquals("avme", jsonBoxinfo.getOem());
        Assert.assertEquals("en", jsonBoxinfo.getLang());
        Assert.assertEquals("B", jsonBoxinfo.getAnnex());
        Assert.assertEquals("", jsonBoxinfo.getLab());
        Assert.assertEquals("049", jsonBoxinfo.getCountry());
        Assert.assertEquals("", jsonBoxinfo.getFlag());
        Assert.assertEquals("", jsonBoxinfo.getUpdateConfig());
    }
}
