package com.tchorek.routes_collector.utils;

import com.tchorek.routes_collector.database.json.RegistrationData;
import com.tchorek.routes_collector.database.model.DailyRecord;
import com.tchorek.routes_collector.database.model.Fugitive;
import com.tchorek.routes_collector.database.model.Registration;
import com.tchorek.routes_collector.message.json.BluetoothData;

import java.util.LinkedHashMap;
import java.util.Map;

public class Mapper {

    private static final Boolean NEW_APPROVAL = null;

    public static DailyRecord mapJsonToObject(BluetoothData input){
        return new DailyRecord(input.getUser(),input.getDeviceName(), Timer.getCurrentTimeInSeconds());
    }

    public static BluetoothData mapObjectToJson(DailyRecord input){
        return new BluetoothData(input.getPhoneNumber(),input.getLocation());
    }

    public static Registration mapJsonToObject(RegistrationData input){
        return new Registration(input.getUserData(), input.getTargetPlace(), input.getDate(), input.getReturnDate(), input.getLatitude(), input.getLongitude(), NEW_APPROVAL);
    }

    public static RegistrationData mapObjectToJson(Registration input){
        return new RegistrationData(input.getPhoneNumber(), input.getTargetPlace(), input.getWalkTimestamp(), input.getReturnDate(), input.getLatitude(), input.getLongitude());
    }

    public static Fugitive mapJsonToFugitive(RegistrationData input){
        return new Fugitive(input.getUserData(), input.getLatitude(), input.getLongitude(), Timer.getCurrentTimeInSeconds(), false);
    }

    public static Map<String, Long> mapFugitivesToMap(Iterable<Fugitive> input){
        Map<String, Long> map = new LinkedHashMap<>();
        input.forEach(fugitive -> map.put(fugitive.getPhoneNumber(),fugitive.getDate()));
        return map;
    }
}
