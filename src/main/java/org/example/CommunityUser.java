package org.example;

import lombok.Getter;

import java.util.Date;

@Getter
public class CommunityUser {
    private long chatId;
    private String firstName;
    private String telegramUsername;
    private Date joinTime;

    public CommunityUser(long chatId, String firstName, String telegramUsername) {
        this.chatId = chatId;
        this.firstName = firstName;
        this.telegramUsername = telegramUsername;
        this.joinTime = new Date();
    }

    public String getFormattedJoinTime() {
        int hours = this.joinTime.getHours();
        int minutes = this.joinTime.getMinutes();
        String timeStr = hours + ":";
        if (minutes < 10) {
            timeStr = timeStr + "0" + minutes;
        } else {
            timeStr += minutes;
        }
        return timeStr;
    }
}
