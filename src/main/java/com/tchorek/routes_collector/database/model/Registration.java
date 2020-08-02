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
    @Column(name = "user_id", nullable = false)
    private String phoneNumber;

    @Column(name = "walk_date", nullable = false)
    private long walkTimestamp;

    @Column(name = "latitude", nullable = false)
    private long latitude;

    @Column(name = "longitude", nullable = false)
    private long longitude;

    @Column(name = "approved")
    private Boolean approved;

    @Override
    public String toString() {
        return "Registration" + phoneNumber + " " + Instant.ofEpochSecond(walkTimestamp)
                + latitude + ", " + longitude+ " "+ approved +"\n";
    }
}
