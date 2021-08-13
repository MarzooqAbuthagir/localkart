package com.kart.model;

public class ContactDetailsData {
    private String phoneNo;
    private String mobileNo;
    private String altNo;
    private String whatsappNo;
    private String emailId;
    private String website;
    private String facebook;
    private String vcard;

    public ContactDetailsData(String phoneNo, String mobileNo, String altNo, String whatsappNo, String emailId, String website, String facebook, String vcard) {
        this.phoneNo = phoneNo;
        this.mobileNo = mobileNo;
        this.altNo = altNo;
        this.whatsappNo = whatsappNo;
        this.emailId = emailId;
        this.website = website;
        this.facebook = facebook;
        this.vcard = vcard;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getAltNo() {
        return altNo;
    }

    public void setAltNo(String altNo) {
        this.altNo = altNo;
    }

    public String getWhatsappNo() {
        return whatsappNo;
    }

    public void setWhatsappNo(String whatsappNo) {
        this.whatsappNo = whatsappNo;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getFacebook() {
        return facebook;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    public String getVcard() {
        return vcard;
    }

    public void setVcard(String vcard) {
        this.vcard = vcard;
    }
}
