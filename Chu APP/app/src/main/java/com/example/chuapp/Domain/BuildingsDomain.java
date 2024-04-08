package com.example.chuapp.Domain;

import android.net.Uri;

public class BuildingsDomain {
    private String title;
    private Uri imageUrl;
    private String buildingAbbreviation;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Uri getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(Uri imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getBuildingAbbreviation() {
        return buildingAbbreviation;
    }

    public void setBuildingAbbreviation(String buildingAbbreviation) {
        this.buildingAbbreviation = buildingAbbreviation;
    }

    public BuildingsDomain(String title, Uri imageUrl, String buildingAbbreviation) {
        this.title = title;
        this.imageUrl = imageUrl;
        this.buildingAbbreviation = buildingAbbreviation;
    }
}
