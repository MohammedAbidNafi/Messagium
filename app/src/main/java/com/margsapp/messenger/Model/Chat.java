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

    public Chat(String sender, String receiver, String message, String isseen, String timestamp, String replytext, String reply,String replyto) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.isseen = isseen;
        this.timestamp = timestamp;
        this.replytext = replytext;
        this.reply = reply;
        this.replyto = replyto;

    }

    public Chat() {
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
