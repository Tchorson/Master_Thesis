package com.tchorek.routes_collector.utils;

import com.tchorek.routes_collector.database.model.Track;
import com.tchorek.routes_collector.database.json.BluetoothDataJSON;

import java.time.Instant;

public class TrackMapper {

    public static Track mapJsonToObject(BluetoothDataJSON input){
        return new Track(input.getUser(),input.getLocation(), Instant.now().getEpochSecond());
    }

    public static BluetoothDataJSON mapObjectToJson(Track input){
        return new BluetoothDataJSON(input.getPhoneNumber(),input.getLocation());
    }
}
