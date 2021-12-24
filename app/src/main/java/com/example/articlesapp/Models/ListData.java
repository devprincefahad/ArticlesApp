package com.example.articlesapp.Models;

public class ListData {
    private String articleHeadline;
    private String articleData;
    private String articleImage;
    private String objectId;
    public ListData() {}

    public ListData(String objectId, String articleHeadline,
                    String articleData, String articleImage) {
        this.objectId = objectId;
        this.articleHeadline = articleHeadline;
        this.articleData = articleData;
        this.articleImage = articleImage;
    }

    public String getArticleHeadline() {
        return articleHeadline;
    }

    public void setArticleHeadline(String articleHeadline) {
        this.articleHeadline = articleHeadline;
    }
    public String getArticleData() {
        return articleData;
    }
    public void setArticleData(String articleData) {
        this.articleData = articleData;
    }
    public String getArticleImage() {
        return articleImage;
    }
    public void setArticleImage(String articleImage) {
        this.articleImage = articleImage;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }
}
