package com.margsapp.messenger.Model;

import java.util.List;

public class Group {


    private String groupname;
    private String imageUrl;
    private String id;
    private String user_name;


    public Group(String groupname, String imageUrl, String id, String user_name) {
        this.groupname = groupname;
        this.imageUrl = imageUrl;
        this.id = id;
        this.user_name = user_name;



    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
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
