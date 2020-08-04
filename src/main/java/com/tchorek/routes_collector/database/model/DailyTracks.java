package com.tchorek.routes_collector.database.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@AllArgsConstructor
@Entity
@Table(name = "user_routes_daily")
public class DailyTracks extends  BaseTrack{

    public DailyTracks(String phoneNumber, String location, long date) {
        super(phoneNumber, location, date);
    }
}
