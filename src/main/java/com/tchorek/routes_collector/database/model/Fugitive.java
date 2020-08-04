package com.tchorek.routes_collector.database.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.Instant;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(name = "fugitives")
public class Fugitive {

    @Id
    @Column(name = "user_id", nullable = false)
    private String phoneNumber;

    @Column(name = "latitude")
    private Long latitude;

    @Column(name = "longitude")
    private Long longitude;

    @Column(name = "escape_date", nullable = false)
    private long date;

    @Override
    public String toString() {
        return "Track" + phoneNumber + " " + latitude + " " + Instant.ofEpochSecond(longitude)+ " "+ date +"\n";
    }
}
