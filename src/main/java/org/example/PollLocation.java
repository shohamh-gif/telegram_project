package org.example;

public class PollLocation {
    final long chatId;
    final int messageId;

    public PollLocation(long chatId, int messageId) {
        this.chatId = chatId;
        this.messageId = messageId;
    }
}
