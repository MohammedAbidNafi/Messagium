package com.margsapp.messenger.Model;

public class Group {


    private String groupname;
    private String imageUrl;

    public Group(String groupname, String imageUrl) {
        this.groupname = groupname;
        this.imageUrl = imageUrl;


    }

    public Group(){}

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getGroupname() {
        return groupname;
    }

    public void setGroupname(String groupname) {
        this.groupname = groupname;
    }
}
