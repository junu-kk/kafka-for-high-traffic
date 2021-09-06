package com.example.fromkafkatoes.entity;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
public class Test {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String _id;

    private String timestamp;
    private String userAgent;
    private String colorName;
    private String userName;

    @Builder
    public Test(String _id, String timestamp, String userAgent, String colorName, String userName) {
        this._id = _id;
        this.timestamp = timestamp;
        this.userAgent = userAgent;
        this.colorName = colorName;
        this.userName = userName;
    }
}
