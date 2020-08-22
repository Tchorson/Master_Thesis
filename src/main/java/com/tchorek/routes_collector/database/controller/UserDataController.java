package com.tchorek.routes_collector.database.controller;

import com.tchorek.routes_collector.message.json.BluetoothData;
import com.tchorek.routes_collector.database.json.ServerData;
import com.tchorek.routes_collector.database.service.DatabaseService;
import com.tchorek.routes_collector.monitoring.service.MonitoringService;
import com.tchorek.routes_collector.utils.Mapper;
import com.tchorek.routes_collector.utils.Timer;
import com.tchorek.routes_collector.utils.Validator;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Log4j2
@Controller
public class UserDataController {

    @Autowired
    DatabaseService databaseService;

    @Autowired
    MonitoringService monitoringService;

    @Autowired
    Validator validator;

    @PostMapping(path = "/point", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity saveUserLocation(@RequestBody BluetoothData data){
        return analyzeData(data);
    }

    @GetMapping(path = "/daily-data")
    public ResponseEntity getDailyData(){
        return ResponseEntity.ok().body(databaseService.getDailyData());
    }

    @GetMapping(path = "/find-users", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getUsersWhoMetUserRecently(@RequestBody ServerData phoneWithTime){
        return ResponseEntity.ok().body(databaseService.getUsersWhoMetUser(phoneWithTime));
    }

    @GetMapping(path = "/user-daily-route", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getUserRoute(@RequestBody String phoneNumber){
        return ResponseEntity.ok().body(databaseService.getUserDailyRoute(phoneNumber));
    }

    @GetMapping(path = "/user-history", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getUserHistory(@RequestBody String phoneNumber){
        return ResponseEntity.ok().body(databaseService.getUserHistory(phoneNumber));
    }

    @GetMapping(path = "/users-data", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getAllUsersFromParticularPlaceAndTime(@RequestBody ServerData locationAndTime){
        return ResponseEntity.ok().body(databaseService.getAllUsersFromParticularPlaceAndTime(locationAndTime.getUserData(), locationAndTime.getStartDate(), locationAndTime.getStopDate()));
    }

    private ResponseEntity analyzeData(BluetoothData data){
        String deviceName = data.getDeviceName();
        String user = data.getUser();
        if(validator.isDeviceValid(deviceName) && monitoringService.checkIfUserIsRegistered(user)){
            if(monitoringService.isUserBeforeTime(user)){
                log.warn("USER {} ARRIVED BEFORE SCHEDULED TIME: {}", user, Timer.getCurrentTimeInSeconds());
            }

            databaseService.saveTrackOfUser(Mapper.mapJsonToObject(data));
            monitoringService.saveUserActivity(Mapper.mapJsonToObject(data));
            log.debug("USER {} REACHED {} DEVICE", user, Timer.getCurrentTimeInSeconds());
            return ResponseEntity.ok(HttpStatus.OK);
        }
        if(validator.isDeviceValid(deviceName)){
            log.warn("UNAUTHORIZED USER IN THE AREA: {} at time {}", user, Timer.getFullDate(Timer.getCurrentTimeInSeconds()));
        }
        else {
            log.warn("UNKNOWN DEVICE ATTEMPT  {} at time {}", deviceName, Timer.getFullDate(Timer.getCurrentTimeInSeconds())) ;
        }
        return ResponseEntity.ok(HttpStatus.UNAUTHORIZED);

    }
}
