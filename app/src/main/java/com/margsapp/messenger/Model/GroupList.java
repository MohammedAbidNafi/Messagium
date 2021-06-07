package com.margsapp.messenger.Model;

public class GroupList {

    private String id;
    private String admin;
    private String groupname;
    private String groupid;

    public GroupList(String id, String admin, String groupname,String groupid) {
        this.id = id;
        this.admin = admin;
        this.groupname = groupname;
        this.groupid = groupid;
    }

    public GroupList() {
    }
    public String getGroupid() {
        return groupid;
    }

    public void setGroupid(String groupid) {
        this.groupid = groupid;
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