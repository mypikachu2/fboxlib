package org.jfritz.fboxlib.fritzbox;

import org.jfritz.fboxlib.exceptions.FirmwareNotDetectedException;
import org.jfritz.fboxlib.exceptions.PageNotFoundException;
import org.jfritz.fboxlib.internal.helper.HTMLUtil;

import java.io.IOException;
import java.util.Vector;

public class SystemStatus {

    protected FirmwareVersion fwVersion = null;

    public FirmwareVersion getFWVersionFromSystemStatus(final FritzBoxCommunication fbc)
            throws IOException, FirmwareNotDetectedException, PageNotFoundException {
        String response = fbc.getSystemStatus();
        parseResponse(response);

        return fwVersion;
    }

    public FirmwareVersion getFirmwareVersion() {
        return fwVersion;
    }

    protected void parseResponse(final String input)
            throws FirmwareNotDetectedException {
        fwVersion = new FirmwareVersion();

        if (input == null) {
            throw new FirmwareNotDetectedException("Could not parse input, you submitted NULL");
        }

        String response = input;

        if (response.indexOf("<br>") > 0) {
            // remove all what is behind a <br> tag
            response = response.substring(0, response.indexOf("<br>"));
        }

        String inputWithoutHtmlTags = HTMLUtil.stripHTMLTags(response.replaceAll("\n", ""));
        String[] splitted = inputWithoutHtmlTags.split("-");
        Vector<String> splittedAndCleaned = new Vector<String>(splitted.length);
        for (int i = 0; i < splitted.length; i++) {
            if (splitted[i].contains("overwrite")) {
                // ignore this field
            } else {
                // add other, non-"overwrite" fields
                String a = splitted[i];
                splittedAndCleaned.add(a);
            }
        }

        if (splittedAndCleaned.size() != 10 && splittedAndCleaned.size() != 11 && splittedAndCleaned.size() < 11) {
            throw new FirmwareNotDetectedException("Expected response with 10 or 11 fields, but got " + splittedAndCleaned.size());
        }
        extractFirmwareValues(splittedAndCleaned);
    }

    private void extractFirmwareValues(final Vector<String> splitted)
            throws FirmwareNotDetectedException {
        fwVersion.setName(splitted.get(0));
        fwVersion.setAnnex(splitted.get(1));

        extractFirmwareVersion(splitted.get(7));
        extractRevision(splitted.get(8));
        fwVersion.setBranding(splitted.get(9));
        extractLanguage(splitted);
    }

    private void extractFirmwareVersion(final String input)
            throws FirmwareNotDetectedException {
        int fwLength = input.length();

        try {
            fwVersion.setMinor(Byte.parseByte(input.substring(fwLength - 2)));
            fwVersion.setMajor(Byte.parseByte(input.substring(fwLength - 4, fwLength - 2)));
            fwVersion.setBoxType(Integer.parseInt(input.substring(0, fwLength - 4)));
        } catch (NumberFormatException nfe) {
            throw new FirmwareNotDetectedException("Could not convert firmware string to byte: " + input);
        } catch (StringIndexOutOfBoundsException e) {
            throw new FirmwareNotDetectedException("Could not convert firmware string to byte: " + input);
        }
    }

    private void extractRevision(final String input)
            throws FirmwareNotDetectedException {
        try {
            fwVersion.setRevision(Integer.parseInt(input));
        } catch (NumberFormatException nfe) {
            throw new FirmwareNotDetectedException("Could not convert revision string to byte: " + input);
        }
    }

    private void extractLanguage(final Vector<String> splitted) {
        fwVersion.setLanguage("de");

        if (splitted.size() >= 11) {
            if (splitted.get(10).length() >= 2) {
                fwVersion.setLanguage(splitted.get(10).substring(0, 2));
            }
        }
    }
}
