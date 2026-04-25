package com.example.personaltutoringservice;

public class ChatMessage {
    private String sender;
    private String receiver;
    private String message;
    private Long timestamp;

    public ChatMessage(String sender, String receiver, String message, Long timestamp) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getSender() { return sender; }
    public String getReceiver() { return receiver; }
    public String getMessage() { return message; }
    public Long getTimestamp() { return timestamp; }
}
