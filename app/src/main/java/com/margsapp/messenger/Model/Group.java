package com.margsapp.messenger.Model;

import java.util.List;

public class Group {


    private String groupname;
    private String imageUrl;
    private String id;


    public Group(String groupname, String imageUrl, String id) {
        this.groupname = groupname;
        this.imageUrl = imageUrl;
        this.id = id;



    }

    public Group(){}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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
