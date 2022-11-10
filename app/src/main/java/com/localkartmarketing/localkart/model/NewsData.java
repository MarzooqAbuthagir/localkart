package com.localkartmarketing.localkart.model;

public class NewsData {
    private String newsId;
    private String newsDetail;
    private String actionType;
    private String dataLink;

    public NewsData(String newsId, String newsDetails, String actionType, String dataLink) {
        this.newsId = newsId;
        this.newsDetail = newsDetails;
        this.actionType = actionType;
        this.dataLink = dataLink;
    }

    public String getNewsId() {
        return newsId;
    }

    public void setNewsId(String newsId) {
        this.newsId = newsId;
    }

    public String getNewsDetail() {
        return newsDetail;
    }

    public void setNewsDetail(String newsDetail) {
        this.newsDetail = newsDetail;
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
