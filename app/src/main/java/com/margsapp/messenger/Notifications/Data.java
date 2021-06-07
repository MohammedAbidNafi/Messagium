package com.margsapp.messenger.Notifications;

public class Data {

    private String user;
    private int icon;
    private String body;
    private String title;
    private String sented;
    private String group;
    //private String groupid;

    public Data(String user, int icon, String body, String title, String sented,String group) {
        this.user = user;
        this.icon = icon;
        this.body = body;
        this.title = title;
        this.sented = sented;
        this.group = group;
       // this.groupid = groupid;
    }

    public Data(){

    }
    /*
    public String getGroupid() {
        return groupid;
    }
     */

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSented() {
        return sented;
    }

    public void setSented(String sented) {
        this.sented = sented;
    }
}
