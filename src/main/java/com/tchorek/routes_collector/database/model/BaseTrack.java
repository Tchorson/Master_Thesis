package com.tchorek.routes_collector.database.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.Instant;

@Getter
@Entity
@RequiredArgsConstructor
@AllArgsConstructor
@NoArgsConstructor
public abstract class BaseTrack {

    @Id
    @Column(name = "user_id")
    private String phoneNumber;

    @Column(name = "device_id")
    private String location;

    @Column(name = "timestamp")
    private long date;

    @Override
    public String toString() {
        return "Track" + phoneNumber + " " + location + " " + Instant.ofEpochSecond(date) +"\n";
    }
}
