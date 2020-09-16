package com.tchorek.routes_collector.database.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@AllArgsConstructor
@Setter
@Entity
@Table(name = "user_routes_daily")
public class DailyRecord extends  BaseTrack{

    public DailyRecord(String phoneNumber, String location, long date) {
        super(phoneNumber, location, date);
    }
}
