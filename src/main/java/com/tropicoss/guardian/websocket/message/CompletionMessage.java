package com.tropicoss.guardian.websocket.message;

public class CompletionMessage {
    private String type = "completion";
    public String content;
    public boolean success;
    public String origin;
    public String command;

    public CompletionMessage(String origin, String content, boolean success, String command) {
        this.origin = origin;
        this.content = content;
        this.success = success;
        this.command = command;
    }
}
