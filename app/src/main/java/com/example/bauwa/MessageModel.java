package com.example.bauwa;

public class MessageModel {

    String msg, receiver, sender;

    public MessageModel() {

    }

    public MessageModel(String msg, String receiver, String sender) {
        this.msg = msg;
        this.receiver = receiver;
        this.sender = sender;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
}
