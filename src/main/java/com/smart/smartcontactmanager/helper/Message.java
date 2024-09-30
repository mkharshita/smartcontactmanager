package com.smart.smartcontactmanager.helper;

public class Message {

    private String content;
    private String typeString;
    
    public Message() {
    }

    public Message(String content, String typeString) {
        this.content = content;
        this.typeString = typeString;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
    
    public String getTypeString() {
        return typeString;
    }
    
    public void setTypeString(String typeString) {
        this.typeString = typeString;
    }
    
}
