package com.margsapp.messenger.Model;

import java.util.List;

public class Group {


    private String groupname;
    private String imageUrl;
    private String id;
    private String user_name;
    private String admin;
    private String groupid;
    private String createdon;

    public Group(String groupname, String imageUrl, String id, String user_name, String admin, String createdon,String groupid) {
        this.groupname = groupname;
        this.imageUrl = imageUrl;
        this.id = id;
        this.user_name = user_name;
        this.admin = admin;
        this.createdon = createdon;
        this.groupid = groupid;



    }

    public String getGroupid() {
        return groupid;
    }

    public void setGroupid(String groupid) {
        this.groupid = groupid;
    }

    public String getCreatedon() {
        return createdon;
    }

    public void setCreatedon(String createdon) {
        this.createdon = createdon;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
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
