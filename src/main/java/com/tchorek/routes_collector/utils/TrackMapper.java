package com.tchorek.routes_collector.utils;

import com.tchorek.routes_collector.database.model.Track;
import com.tchorek.routes_collector.database.model.TrackJSON;

import java.time.Instant;

public class TrackMapper {

    public static Track mapJsonToObject(TrackJSON input){
        return new Track(input.getNumber(),input.getLocation(), Instant.now().getEpochSecond());
    }

    public static TrackJSON mapObjectToJson(Track input){
        return new TrackJSON(input.getPhoneNumber(),input.getLocation());
    }
}
