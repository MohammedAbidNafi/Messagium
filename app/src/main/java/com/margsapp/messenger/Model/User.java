package com.margsapp.messenger.Model;

public class User {

    private String id;
    private String username;
    private String imageURL;
    private String status;
    private String DT;

    private String joined_on;
    public User(String id, String username, String imageUrl, String status, String DT, String joined_on)
    {
        this.id = id;
        this.username = username;
        this.imageURL = imageUrl;
        this.status = status;
        this.DT = DT;
        this.joined_on = joined_on;

    }

    public User()
    {

    }

    public String getJoined_on() {
        return joined_on;
    }

    public void setJoined_on(String joined_on) {
        this.joined_on = joined_on;
    }

    public String getDt() {
        return DT;
    }

    public void setDt(String dt) {
        this.DT = DT;
    }

    public  String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageUrl() {
        return imageURL;
    }

    public void setImageUrl(String imageUrl) {
        this.imageURL = imageUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }





}
