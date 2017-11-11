package org.jfritz.fboxlib.fritzbox;

import org.jfritz.fboxlib.exceptions.FirmwareNotDetectedException;
import org.jfritz.fboxlib.exceptions.PageNotFoundException;
import org.w3c.dom.CharacterData;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;

public class JSonBoxinfo {

    private String name = "not_detected";
    private String hw = "not_detected";
    private String version = "not_detected";
    private String revision = "not_detected";
    private String serial = "not_detected";
    private String oem = "not_detected";
    private String lang = "not_detected";
    private String annex = "not_detected";
    private String lab = "not_detected";
    private String country = "not_detected";
    private String flag = "not_detected";
    private String updateConfig = "not_detected";

    public String getName() {
        return name;
    }

    public String getHw() {
        return hw;
    }

    public String getVersion() {
        return version;
    }

    public String getRevision() {
        return revision;
    }

    public String getSerial() {
        return serial;
    }

    public String getOem() {
        return oem;
    }

    public String getLang() {
        return lang;
    }

    public String getAnnex() {
        return annex;
    }

    public String getLab() {
        return lab;
    }

    public String getCountry() {
        return country;
    }

    public String getFlag() {
        return flag;
    }

    public String getUpdateConfig() {
        return updateConfig;
    }

    public JSonBoxinfo(final FritzBoxCommunication fbc)
            throws IOException, PageNotFoundException,
            FirmwareNotDetectedException {
        String response = fbc.getJsonBoxInfo();
        if (response != null && !"".equals(response)) {
            parseResponse(response);
        }
    }

    private void parseResponse(String response) {
        try {
            DocumentBuilder db = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(response));

            Document doc = db.parse(is);
            NodeList nodes = doc.getElementsByTagName("j:BoxInfo");

            for (int i = 0; i < nodes.getLength(); i++) {
                Element element = (Element) nodes.item(i);

                this.name = getCharacterDataFromElement((Element) element.getElementsByTagName("j:Name").item(0));
                this.hw = getCharacterDataFromElement((Element) element.getElementsByTagName("j:HW").item(0));
                this.version = getCharacterDataFromElement((Element) element.getElementsByTagName("j:Version").item(0));
                this.revision = getCharacterDataFromElement((Element) element.getElementsByTagName("j:Revision").item(0));
                this.serial = getCharacterDataFromElement((Element) element.getElementsByTagName("j:Serial").item(0));
                this.oem = getCharacterDataFromElement((Element) element.getElementsByTagName("j:OEM").item(0));
                this.lang = getCharacterDataFromElement((Element) element.getElementsByTagName("j:Lang").item(0));
                this.annex = getCharacterDataFromElement((Element) element.getElementsByTagName("j:Annex").item(0));
                this.lab = getCharacterDataFromElement((Element) element.getElementsByTagName("j:Lab").item(0));
                this.country = getCharacterDataFromElement((Element) element.getElementsByTagName("j:Country").item(0));
                this.flag = getCharacterDataFromElement((Element) element.getElementsByTagName("j:Flag").item(0));
                this.updateConfig = getCharacterDataFromElement((Element) element.getElementsByTagName("j:UpdateConfig").item(0));
            }

        } catch (SAXException e) {
            // input is not XML, or other parse error, just ignore
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    private static String getCharacterDataFromElement(Element e) {
        if (e != null) {
            Node child = e.getFirstChild();
            if (child instanceof CharacterData) {
                CharacterData cd = (CharacterData) child;
                return cd.getData();
            }
        }
        return "";
    }
}
