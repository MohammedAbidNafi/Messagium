package com.margsapp.messenger.Model;

public class Chat {

    private String sender;
    private String receiver;
    private String message;

    private String isseen;

    private String timestamp;

    private String replytext;
    private String reply;
    private String replyto;

    private String sendername;

    private String replyname;

    private String encrypted;

    public Chat(String sender, String receiver, String message, String isseen, String timestamp, String replytext, String reply,String replyto,String replyname, String sendername, String encrypted) {

        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.isseen = isseen;
        this.timestamp = timestamp;
        this.replytext = replytext;
        this.reply = reply;
        this.replyto = replyto;
        this.replyname = replyname;
        this.sendername = sendername;
        this.encrypted = encrypted;

    }

    public Chat() {
    }

    public String getEncrypted() {
        return encrypted;
    }

    public void setEncrypted(String encrypted) {
        this.encrypted = encrypted;
    }

    public String getSendername() {
        return sendername;
    }

    public void setSendername(String sendername) {
        this.sendername = sendername;
    }

    public String getReplyname() {
        return replyname;
    }

    public void setReplyname(String replyname) {
        this.replyname = replyname;
    }

    public String getReplyto() {
        return replyto;
    }

    public void setReplyto(String replyto) {
        this.replyto = replyto;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public String getReplytext() {
        return replytext;
    }

    public void setReplytxt(String replytxt) {
        this.replytext = replytxt;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getIsseen() {
        return isseen;
    }

    public void setIsseen(String isseen) {
        this.isseen = isseen;
    }
}
