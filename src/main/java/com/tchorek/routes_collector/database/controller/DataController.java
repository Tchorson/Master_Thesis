package com.tchorek.routes_collector.database.controller;

import com.tchorek.routes_collector.message.json.BluetoothData;
import com.tchorek.routes_collector.database.json.ServerData;
import com.tchorek.routes_collector.database.service.DatabaseService;
import com.tchorek.routes_collector.monitoring.service.MonitoringService;
import com.tchorek.routes_collector.utils.Mapper;
import com.tchorek.routes_collector.utils.Validator;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@Log4j2
@Controller
public class DataController {

    @Autowired
    DatabaseService databaseService;

    @Autowired
    MonitoringService monitoringService;

    @PostMapping(path = "/save-user-track", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity saveUserTrack(@RequestBody BluetoothData data){
        if(Validator.DeviceValidator.isDeviceValid(data.getLocation()) && monitoringService.checkIfUserIsRegisteredAndEligibleForWalk(data.getUser())){
            databaseService.saveTrackOfUser(Mapper.mapJsonToObject(data));
            monitoringService.saveUserActivity(Mapper.mapJsonToObject(data));
            return ResponseEntity.ok(HttpStatus.OK);
        }
        log.warn("UNAUTHORIZED USER IN THE AREA: {} at time {}",data.getUser(), Instant.ofEpochSecond(Instant.now().getEpochSecond()));
        return ResponseEntity.ok(HttpStatus.UNAUTHORIZED);
    }

    @GetMapping(path = "/all-data")
    public ResponseEntity getAllData(){
        return ResponseEntity.ok().body(databaseService.getAllData());
    }

    @GetMapping(path = "/find-users", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getUsersWhoMetUserRecently(@RequestBody ServerData phoneWithTime){
        return ResponseEntity.ok().body(databaseService.getUsersWhoMetUserRecently(phoneWithTime.getUserData(), phoneWithTime.getStartDate(), phoneWithTime.getStopDate()));
    }

    @GetMapping(path = "/all-user-data", consumes = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity getUserRoute(@RequestBody String phoneNumber){
        return ResponseEntity.ok().body(databaseService.getUserRoute(phoneNumber));
    }

    @GetMapping(path = "/all-user-history", consumes = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity getUserHistory(@RequestBody String phoneNumber){
        return ResponseEntity.ok().body(databaseService.getUserHistory(phoneNumber));
    }

    @GetMapping(path = "/user-route", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getUserRouteFromParticularTime(@RequestBody ServerData phoneWithTime) {
        return ResponseEntity.ok().body(databaseService.getUserRouteFromParticularTime(phoneWithTime.getUserData(), phoneWithTime.getStartDate(), phoneWithTime.getStopDate()));
    }

    @GetMapping(path = "/users-time", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getAllUsersFromParticularPlaceAndTime(@RequestBody ServerData locationAndTime){
        return ResponseEntity.ok().body(databaseService.getAllUsersFromParticularPlaceAndTime(locationAndTime.getUserData(), locationAndTime.getStartDate(), locationAndTime.getStopDate()));
    }
}
