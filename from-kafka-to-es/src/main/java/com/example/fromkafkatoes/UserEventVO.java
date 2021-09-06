package com.example.fromkafkatoes;

import lombok.Getter;
import lombok.ToString;

@Getter
public class UserEventVO {
    private String timestamp;
    private String userAgent;
    private String colorName;
    private String userName;

    public UserEventVO(String timestamp, String userAgent, String colorName, String userName) {
        this.timestamp = timestamp;
        this.userAgent = userAgent;
        this.colorName = colorName;
        this.userName = userName;
    }

    @Override
    public String toString() {
        return "UserEventVO{" +
                "timestamp='" + timestamp + '\'' +
                ", userAgent='" + userAgent + '\'' +
                ", colorName='" + colorName + '\'' +
                ", userName='" + userName + '\'' +
                '}';
    }
}
