package com.example.chuapp.Domain;

public class EventsDomain {
    private String date;
    private String title;
    private String location;
    private String link;

    public EventsDomain(String date, String title, String location, String link) {
        this.date = date;
        this.title = title;
        this.location = location;
        this.link = link;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String getLocation) {
        this.location = location;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
