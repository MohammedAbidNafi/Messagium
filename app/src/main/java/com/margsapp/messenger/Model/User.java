package com.margsapp.messenger.Model;

public class User {

    private String id;
    private String username;
    private String imageURL;
    private String status;
    private String DT;
    private String typingto;

    private String lastseen;

    private String version_name;

    private String joined_on;
    private String phoneno;

    public User(String id, String username, String imageUrl, String status, String DT, String joined_on, String typingto, String lastseen,String version_name,String phoneno)
    {
        this.id = id;
        this.username = username;
        this.imageURL = imageUrl;
        this.status = status;
        this.DT = DT;
        this.joined_on = joined_on;
        this.typingto = typingto;
        this.lastseen = lastseen;
        this.version_name = version_name;
        this.phoneno = phoneno;
    }

    public User()
    {

    }

    public String getPhoneno() {
        return phoneno;
    }

    public void setPhoneno(String phoneno) {
        this.phoneno = phoneno;
    }

    public String getLastseen() {
        return lastseen;
    }

    public void setLastseen(String lastseen) {
        this.lastseen = lastseen;
    }

    public String getVersion_name() {
        return version_name;
    }

    public void setVersion_name(String version_name) {
        this.version_name = version_name;
    }

    public String getTypingto() {
        return typingto;
    }

    public void setTypingto(String typingto) {
        this.typingto = typingto;
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
