package com.example.chuapp.Domain;

public class NewsDomain {


    private String type;
    private String title;
    private String link;

    public NewsDomain(String type, String title, String link) {
        this.type = type;
        this.title = title;
        this.link = link;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() { return link; }

    public void setLink(String link) { this.link = link; }
}
