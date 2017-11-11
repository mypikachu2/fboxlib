package org.jfritz.fboxlib.fritzbox;

import org.jfritz.fboxlib.exceptions.InvalidCredentialsException;
import org.jfritz.fboxlib.exceptions.InvalidSessionIdException;
import org.jfritz.fboxlib.exceptions.LoginBlockedException;
import org.jfritz.fboxlib.exceptions.PageNotFoundException;

import java.io.IOException;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NetworkMethods {

    private static final String QUERY_GET_WIFI_STATUS_2GHZ = "wlan:settings/ap_enabled";
    private static final String QUERY_GET_WIFI_STATUS_5GHZ = "wlan:settings/ap_enabled_scnd";
    private static final String QUERY_GET_GUEST_AP_STATUS = "wlan:settings/guest_ap_enabled";
    private static final String QUERY_GET_SSID_GUEST = "wlan:settings/guest_ssid";
    private static final String QUERY_GET_SSID_2GHZ = "wlan:settings/ssid";
    private static final String QUERY_GET_SSID_5GHZ = "wlan:settings/ssid_scnd";
    private static final String QUERY_GET_MAC_ADDRESS = "env:settings/macdsl";
    private static final String QUERY_EXTERNAL_IP = "connection0:status/ip";
    private static final String PARSE_MAC_ADDRESS = "<UDN>uuid:([^<]*)</UDN>";
    private static final String PARSE_CONTROL_URL = "<controlURL>/(igdupnp|upnp?)/[^/]*/[^/]*</controlURL>";

    private FritzBoxCommunication fbc;

    public NetworkMethods(FritzBoxCommunication fbc) {
        this.fbc = fbc;
    }

    public String getWiFiStatus2GHZ() throws IOException, LoginBlockedException, InvalidCredentialsException, PageNotFoundException, InvalidSessionIdException {
        Vector<String> query = new Vector<String>();
        query.add(QUERY_GET_WIFI_STATUS_2GHZ);

        Vector<String> response = fbc.getQuery(query);
        if (response.size() == 1) {
            return response.get(0);
        } else {
            return "ERROR: Could not get WiFi status";
        }
    }

    public String getWiFiStatus5GHZ() throws IOException, LoginBlockedException, InvalidCredentialsException, PageNotFoundException, InvalidSessionIdException {
        Vector<String> query = new Vector<String>();
        query.add(QUERY_GET_WIFI_STATUS_5GHZ);

        Vector<String> response = fbc.getQuery(query);
        if (response.size() == 1) {
            return response.get(0);
        } else {
            return "ERROR: Could not get WiFi status";
        }
    }

    public String getGuestAPStatus() throws IOException, LoginBlockedException, InvalidCredentialsException, PageNotFoundException, InvalidSessionIdException {
        Vector<String> query = new Vector<String>();
        query.add(QUERY_GET_GUEST_AP_STATUS);

        Vector<String> response = fbc.getQuery(query);
        if (response.size() == 1) {
            return response.get(0);
        } else {
            return "ERROR: Could not get guest AP WiFi status";
        }
    }

    public String getSSIDGuest() throws IOException, LoginBlockedException, InvalidCredentialsException, PageNotFoundException, InvalidSessionIdException {
        Vector<String> query = new Vector<String>();
        query.add(QUERY_GET_SSID_GUEST);

        Vector<String> response = fbc.getQuery(query);
        if (response.size() == 1) {
            return response.get(0);
        } else {
            return "ERROR: Could not get SSID for Guest WiFi";
        }
    }

    public String getSSID2GHZ() throws IOException, LoginBlockedException, InvalidCredentialsException, PageNotFoundException, InvalidSessionIdException {
        Vector<String> query = new Vector<String>();
        query.add(QUERY_GET_SSID_2GHZ);

        Vector<String> response = fbc.getQuery(query);
        if (response.size() == 1) {
            return response.get(0);
        } else {
            return "ERROR: Could not get SSID for 2.4 GHz WiFi";
        }
    }

    public String getSSID5GHZ() throws IOException, LoginBlockedException, InvalidCredentialsException, PageNotFoundException, InvalidSessionIdException {
        Vector<String> query = new Vector<String>();
        query.add(QUERY_GET_SSID_5GHZ);

        Vector<String> response = fbc.getQuery(query);
        if (response.size() == 1) {
            return response.get(0);
        } else {
            return "ERROR: Could not get SSID for 5 GHz WiFi";
        }
    }

    public String getExternalIP() throws IOException, LoginBlockedException, InvalidCredentialsException, PageNotFoundException, InvalidSessionIdException {
        Vector<String> query = new Vector<String>();
        query.add(QUERY_EXTERNAL_IP);

        Vector<String> response = fbc.getQuery(query);
        if (response.size() == 1) {
            return response.get(0);
        } else {
            return "No external IP";
        }
    }

    public String getMacAddress() throws IOException, LoginBlockedException, InvalidCredentialsException, PageNotFoundException, InvalidSessionIdException {
        String macAddress = "00:00:00:00:00:00";

        Vector<String> query = new Vector<String>();
        query.add(QUERY_GET_MAC_ADDRESS);

        Vector<String> response = fbc.getQuery(query);
        if (response.size() == 1) {
            macAddress = response.get(0);
        } else {
            macAddress = getMacFromUPnP();
        }

        return macAddress;
    }

    private String getMacFromUPnP() throws IOException, LoginBlockedException, InvalidCredentialsException, PageNotFoundException, InvalidSessionIdException {
        String mac = "";
        String response = new String();

        String url = "/igddesc.xml";
        try {
            response = fbc.getUpnpUrlAsString(url);
        } catch (InvalidSessionIdException e) {
            fbc.getNewSid();
            response = fbc.getUpnpUrlAsString(url);
        }

        Pattern p = Pattern.compile(PARSE_MAC_ADDRESS);
        Matcher m = p.matcher(response);
        if (m.find()) {
            String resp = m.group(1);
            int idx = resp.lastIndexOf("-");
            String macTmp = resp.substring(idx + 1);
            for (int j = 0; j < macTmp.length(); j++) {
                mac = mac + macTmp.charAt(j);
                if ((j != 0)
                        && (j != macTmp.length() - 1)
                        && ((j - 1) % 2 == 0)) {
                    mac = mac.concat(":");
                }
            }
        }

        return mac;
    }

    public String getUPNPFromIgddesc() throws IOException, LoginBlockedException, InvalidCredentialsException, PageNotFoundException, InvalidSessionIdException {
        String upnp = "upnp";
        String response = new String();
        String url = "/igddesc.xml";

        try {
            response = fbc.getUpnpUrlAsString(url);
        } catch (InvalidSessionIdException e) {
            fbc.getNewSid();
            response = fbc.getUpnpUrlAsString(url);
        }

        Pattern p = Pattern.compile(PARSE_CONTROL_URL);
        Matcher m = p.matcher(response);
        if (m.find() == true) {
            if (m.groupCount() > 0) {
                String res = m.group(1);

                if ("igdupnp".equals(res.toLowerCase())) {
                    upnp = res;
                } else if ("upnp".equals(res.toLowerCase())) {
                    upnp = res;
                } else {
                    upnp = "upnp";
                }

            }
        } else {
            upnp = "upnp";
        }

        return upnp;

    }
}
