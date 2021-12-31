package com.margsapp.messageium.Model;

public class Chatlist {

    public String id;

    public String friends;

    public Chatlist(String id, String friends){
        this.id = id;
        this.friends = friends;
    }

    public Chatlist(){

    }

    public String getFriends() {
        return friends;
    }

    public  String getId(){
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
