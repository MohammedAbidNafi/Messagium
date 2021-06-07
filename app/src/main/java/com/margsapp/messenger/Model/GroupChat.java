package com.margsapp.messenger.Model;

public class GroupChat {

    private String sender;
    private String sendername;


    private String message;

    private String timestamp;

    private String replytext;
    private String reply;
    private String replyto;
    private String replyname;

    private String group;

    public GroupChat(String sender,String sendername, String group, String message, String timestamp, String replytext, String reply, String replyto,String replyname) {

        this.sender = sender;
        this.sendername = sendername;
        this.group = group;
        this.message = message;
        this.timestamp = timestamp;
        this.replytext = replytext;
        this.reply = reply;
        this.replyto = replyto;
        this.replyname = replyname;

    }
    public GroupChat(){


    }

    public String getReplyname() {
        return replyname;
    }

    public void setReplyname(String replyname) {
        this.replyname = replyname;
    }

    public String getSendername() {
        return sendername;
    }

    public void setSendername(String sendername) {
        this.sendername = sendername;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getReplytext() {
        return replytext;
    }

    public void setReplytext(String replytext) {
        this.replytext = replytext;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public String getReplyto() {
        return replyto;
    }

    public void setReplyto(String replyto) {
        this.replyto = replyto;
    }
}
