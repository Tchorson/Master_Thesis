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
@Entity(name = "user_registrations")
public class Registration {

    @Id
    @Column(name = "user_id")
    private String phoneNumber;

    @Column(name = "registration_date")
    private long dateUnixTimestamp;

    @Column(name = "latitude")
    private String latitude;

    @Column(name = "longitude")
    private String longitude;

    @Override
    public String toString() {
        return "Registration" + phoneNumber + Instant.ofEpochSecond(dateUnixTimestamp) + latitude + ", " + longitude +"\n";
    }
}
