package com.margsapp.messenger.Model;

public class User {

    private String id;
    private String username;
    private String imageURL;
    private String status;
    private String DT;


    public User(String id, String username, String imageUrl, String status, String DT)
    {
        this.id = id;
        this.username = username;
        this.imageURL = imageUrl;
        this.status = status;
        this.DT = DT;

    }

    public User()
    {

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
