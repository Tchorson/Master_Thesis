package com.tchorek.routes_collector.database.model;

import com.tchorek.routes_collector.utils.Timer;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "fugitives")
public class Fugitive {

    @Id
    @Column(name = "user_id", nullable = false)
    private String phoneNumber;

    @Column(name = "latitude")
    private Float latitude;

    @Column(name = "longitude")
    private Float longitude;

    @Column(name = "escape_date", nullable = false)
    private long date;

    @Override
    public String toString() {
        return "Track" + phoneNumber + " " + latitude + " " + longitude+ " "+ Timer.getFullDate(date) +"\n";
    }
}
