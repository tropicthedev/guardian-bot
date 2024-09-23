package com.tropicoss.guardian.services.websocket.message;

public class CompletionMessage {
    public String content;
    public boolean success;
    public String origin;
    public String command;
    private final String type = "completion";

    public CompletionMessage(String origin, String content, boolean success, String command) {
        this.origin = origin;
        this.content = content;
        this.success = success;
        this.command = command;
    }
}
