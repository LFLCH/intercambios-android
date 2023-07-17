package com.example.intercambios;

public class Message {
    private String value;
    private String sourceIp;
    private int sourcePort;

    public Message(String sourceIp, int sourcePort, String value) {
        this.value = value;
        this.sourceIp = sourceIp;
        this.sourcePort = sourcePort;
    }

    public String getValue() {
        return value;
    }

    public String getSourceIp() {
        return sourceIp;
    }

    public int getSourcePort() {
        return sourcePort;
    }
}
