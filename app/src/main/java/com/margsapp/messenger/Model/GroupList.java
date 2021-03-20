package com.margsapp.messenger.Model;

public class GroupList {

    private String id;
    private String admin;

    public GroupList(String id, String admin) {
        this.id = id;
        this.admin = admin;
    }

    public GroupList() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }
}
