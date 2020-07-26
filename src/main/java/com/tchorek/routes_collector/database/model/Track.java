package com.tchorek.routes_collector.database.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.Instant;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
@Entity(name = "user_routes")
public class Track {

    @Id
    @Column(name = "user_id")
    private String phoneNumber;

    @Column(name = "device_id")
    private String location;

    @Column(name = "timestamp")
    private long date;

    @Override
    public String toString() {
        return "Track{" +
                "phoneNumber='" + phoneNumber + '\'' +
                ", location='" + location + '\'' +
                ", date=" + date +
                '}';
    }

    public String prettyPrint() {
        return "user with phone number " + phoneNumber +
                " went nearby " + location +
                " at " + Instant.ofEpochSecond(date);
    }
}
