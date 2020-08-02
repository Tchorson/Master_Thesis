package com.tchorek.routes_collector.utils;

import com.tchorek.routes_collector.database.json.RegistrationData;
import com.tchorek.routes_collector.database.model.DailyTracks;
import com.tchorek.routes_collector.database.model.Registration;
import com.tchorek.routes_collector.message.json.BluetoothData;

import java.time.Instant;

public class Mapper {

    private static final boolean APPROVED = true;

    public static DailyTracks mapJsonToObject(BluetoothData input){
        return new DailyTracks(input.getUser(),input.getLocation(), Instant.now().getEpochSecond());
    }

    public static BluetoothData mapObjectToJson(DailyTracks input){
        return new BluetoothData(input.getPhoneNumber(),input.getLocation());
    }

    public static Registration mapJsonToObject(RegistrationData input){
        return new Registration(input.getUserData(), input.getDate(), input.getLatitude(), input.getLongitude(), APPROVED);
    }

    public static RegistrationData mapObjectToJson(Registration input){
        return new RegistrationData(input.getPhoneNumber(), input.getWalkTimestamp(), input.getLatitude(), input.getLongitude());
    }
}
