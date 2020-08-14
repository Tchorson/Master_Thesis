package com.tchorek.routes_collector.database.model;


import com.tchorek.routes_collector.utils.Timer;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
@RequiredArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
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
        return "Track" + phoneNumber + " " + location + " " + Timer.getFullDate(date) +"\n";
    }
}
