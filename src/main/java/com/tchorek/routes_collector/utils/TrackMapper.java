package com.tchorek.routes_collector.utils;

import com.tchorek.routes_collector.database.model.DailyTracks;
import com.tchorek.routes_collector.message.json.BluetoothData;

import java.time.Instant;

public class TrackMapper {

    public static DailyTracks mapJsonToObject(BluetoothData input){
        return new DailyTracks(input.getUser(),input.getLocation(), Instant.now().getEpochSecond());
    }

    public static BluetoothData mapObjectToJson(DailyTracks input){
        return new BluetoothData(input.getPhoneNumber(),input.getLocation());
    }
}
