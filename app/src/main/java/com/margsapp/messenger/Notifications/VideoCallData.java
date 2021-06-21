package com.margsapp.messenger.Notifications;



public class VideoCallData {

    private String sented;

    private String user;

    private String imageUrl;

    private String username;

    private String VideoCall;



    public VideoCallData(String sented,String user, String imageUrl, String username, String VideoCall) {
        this.sented = sented;
        this.user = user;
        this.imageUrl = imageUrl;
        this.username = username;
        this.VideoCall = VideoCall;
    }

    public VideoCallData(){

    }

    public String getSented() {
        return sented;
    }

    public void setSented(String sented) {
        this.sented = sented;
    }

    public String getVideoCall() {
        return VideoCall;
    }

    public void setVideoCall(String videoCall) {
        VideoCall = videoCall;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
