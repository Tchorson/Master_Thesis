package com.tchorek.routes_collector.database.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.time.Instant;

@Getter
@Entity
@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
@RequiredArgsConstructor
@AllArgsConstructor
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
