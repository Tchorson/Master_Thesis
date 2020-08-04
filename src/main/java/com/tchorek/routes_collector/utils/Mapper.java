package com.tchorek.routes_collector.utils;

import com.tchorek.routes_collector.database.json.RegistrationData;
import com.tchorek.routes_collector.database.model.DailyTracks;
import com.tchorek.routes_collector.database.model.Fugitive;
import com.tchorek.routes_collector.database.model.Registration;
import com.tchorek.routes_collector.message.json.BluetoothData;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

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

    public static Fugitive mapJsonToFugitive(RegistrationData input){
        return new Fugitive(input.getUserData(), input.getLatitude(), input.getLongitude(), input.getDate());
    }

    public static Map<String, Long> mapFugitivesToMap(Iterable<Fugitive> input){
        Map<String, Long> map = new LinkedHashMap<>();
        input.forEach(fugitive -> map.put(fugitive.getPhoneNumber(),fugitive.getDate()));
        return map;
    }
}
