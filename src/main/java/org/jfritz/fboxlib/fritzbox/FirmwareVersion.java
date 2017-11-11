package org.jfritz.fboxlib.fritzbox;

public class FirmwareVersion {

    private int boxType = 0;
    private byte major = 0;
    private byte minor = 0;
    private int revision = 0;

    private String branding = "";
    private String name = "";
    private String annex = "";
    private String language = "";

    public FirmwareVersion() {
        this.boxType = 0;
        this.major = 0;
        this.minor = 0;
        this.revision = 0;
    }

    public FirmwareVersion(final int boxType, final byte major, final byte minor) {
        this.boxType = boxType;
        this.major = major;
        this.minor = minor;
    }

    public String toSimpleString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%02d", boxType));
        sb.append(".");
        sb.append(String.format("%02d", major));
        sb.append(".");
        sb.append(String.format("%02d", minor));
        return sb.toString();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name);
        sb.append(", ");
        sb.append(String.format("%02d", boxType));
        sb.append(".");
        sb.append(String.format("%02d", major));
        sb.append(".");
        sb.append(String.format("%02d", minor));
        sb.append(", ");
        sb.append(revision);
        sb.append(", Annex");
        sb.append(annex);
        sb.append(", ");
        sb.append(branding);
        sb.append(", ");
        sb.append(language);
        return sb.toString();
    }

    public int getBoxType() {
        return boxType;
    }

    public void setBoxType(int boxType) {
        this.boxType = boxType;
    }

    public byte getMajor() {
        return major;
    }

    public void setMajor(byte major) {
        this.major = major;
    }

    public byte getMinor() {
        return minor;
    }

    public void setMinor(byte minor) {
        this.minor = minor;
    }

    public int getRevision() {
        return revision;
    }

    public void setRevision(int revision) {
        this.revision = revision;
    }

    public String getBranding() {
        return branding;
    }

    public void setBranding(String branding) {
        this.branding = branding;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAnnex() {
        return annex;
    }

    public void setAnnex(String annex) {
        this.annex = annex;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public final boolean isLowerThan(final int major, final int minor) {
        return (this.major < major || (this.major == major && this.minor < minor));
    }

    public final boolean isLowerThanOrEqual(final int major, final int minor) {
        return (this.major < major || (this.major == major && this.minor <= minor));
    }

    public final boolean isUpperThan(final int major, final int minor) {
        return (this.major > major || (this.major == major && this.minor > minor));
    }

    public final boolean isUpperThanOrEqual(final int major, final int minor) {
        return (this.major > major || (this.major == major && this.minor >= minor));
    }

}
