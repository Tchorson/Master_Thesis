package com.tchorek.routes_collector.utils;

import com.tchorek.routes_collector.database.json.RegistrationData;
import com.tchorek.routes_collector.database.model.DailyRecord;
import com.tchorek.routes_collector.database.model.Fugitive;
import com.tchorek.routes_collector.database.model.Registration;
import com.tchorek.routes_collector.message.json.BluetoothData;
import org.junit.Assert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Profile;

import java.util.Collections;
import java.util.Map;

@Profile("Test")
@ExtendWith(MockitoExtension.class)
class MapperTest {

    private String phoneNumber = "123456789";
    private float longitude = 50.1234567F;
    private float latitude = 20.1945131F;
    private Boolean newApproval = null;
    private String device = "RPI_PARK_01";
    private long currentTime = Timer.getCurrentTimeInSeconds();

    private RegistrationData registrationJson = new RegistrationData(phoneNumber, currentTime, latitude, longitude);
    private BluetoothData bluetoothJson = new BluetoothData(phoneNumber, device);

    private DailyRecord dailyRecordModel = new DailyRecord(phoneNumber, device, currentTime);
    private Registration registrationModel = new Registration(phoneNumber,currentTime, latitude, longitude, null);

    private Fugitive fugitive = new Fugitive(phoneNumber, latitude, longitude, currentTime);
    Iterable<Fugitive> fugitivesCollection = Collections.singletonList(fugitive);

    @Test
    @DisplayName("Given a json , When mapping, Then should return an appropriate model")
    public void mapJsonToModel(){

        Registration registrationModelResult = Mapper.mapJsonToObject(registrationJson);
        DailyRecord dailyRecordResult = Mapper.mapJsonToObject(bluetoothJson);
        Fugitive fugitiveResult = Mapper.mapJsonToFugitive(registrationJson);

        long currentTime = Timer.getCurrentTimeInSeconds();
        Assert.assertEquals(dailyRecordResult, new DailyRecord(phoneNumber, device, currentTime));
        Assert.assertEquals(fugitiveResult, new Fugitive(phoneNumber, latitude, longitude, currentTime));
        Assert.assertEquals(registrationModelResult, new Registration(phoneNumber, currentTime, latitude , longitude , newApproval));
    }

    @Test
    @DisplayName("Given a mode, When mapping, Then should return an appropriate json")
    public void mapModelToJson(){

         BluetoothData bluetoothJsonResult = Mapper.mapObjectToJson(dailyRecordModel);
         RegistrationData registrationDataResult = Mapper.mapObjectToJson(registrationModel);

        Assert.assertEquals(bluetoothJsonResult, bluetoothJson);
        Assert.assertEquals(registrationDataResult, new RegistrationData(phoneNumber, currentTime, latitude, longitude));
    }

    @Test
    @DisplayName("Given a fugitive, When mapping, then should return a map")
    public void mapModelToMap(){

        Map<String, Long> fugtivesMap = Mapper.mapFugitivesToMap(fugitivesCollection);

        Assert.assertEquals(fugtivesMap.size(), 1);
        Assert.assertTrue(fugtivesMap.containsKey(phoneNumber));
        Assert.assertTrue(fugtivesMap.containsValue(currentTime));
    }
}
