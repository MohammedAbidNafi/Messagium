package com.margsapp.messenger.Model;

public class GroupList {

    private String id;
    private String admin;
    private String groupname;

    public GroupList(String id, String admin, String groupname) {
        this.id = id;
        this.admin = admin;
        this.groupname = groupname;
    }

    public GroupList() {
    }

    public String getGroupname() {
        return groupname;
    }

    public void setGroupname(String groupname) {
        this.groupname = groupname;
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
