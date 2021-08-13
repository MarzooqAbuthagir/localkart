package com.kart.model;

public class SilderData {
    private String image;
    private String actionType;
    private String dataLink;

    public SilderData(String image, String actionType, String dataLink) {
        this.image = image;
        this.actionType = actionType;
        this.dataLink = dataLink;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getDataLink() {
        return dataLink;
    }

    public void setDataLink(String dataLink) {
        this.dataLink = dataLink;
    }
}
