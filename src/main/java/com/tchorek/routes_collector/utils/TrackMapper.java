package com.tchorek.routes_collector.utils;

import com.tchorek.routes_collector.database.model.Track;
import com.tchorek.routes_collector.message.model.BluetoothData;

import java.time.Instant;

public class TrackMapper {

    public static Track mapJsonToObject(BluetoothData input){
        return new Track(input.getUser(),input.getLocation(), Instant.now().getEpochSecond());
    }

    public static BluetoothData mapObjectToJson(Track input){
        return new BluetoothData(input.getPhoneNumber(),input.getLocation());
    }
}
