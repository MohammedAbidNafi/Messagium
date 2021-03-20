package com.margsapp.messenger.Model;

public class Group {


    private String groupName;
    private String imageUrl;

    public Group(String groupName, String imageUrl) {
        this.groupName = groupName;
        this.imageUrl = imageUrl;


    }

    public Group(){}

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }



    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
