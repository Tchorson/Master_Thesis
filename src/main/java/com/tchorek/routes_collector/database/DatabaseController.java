package com.tchorek.routes_collector.database;

import com.tchorek.routes_collector.database.model.TrackJSON;
import com.tchorek.routes_collector.database.model.TrackTimeJSON;
import com.tchorek.routes_collector.database.service.DatabaseService;
import com.tchorek.routes_collector.utils.TrackMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Controller
public class DatabaseController {

    @Autowired
    DatabaseService databaseService;

    @PostMapping(path = "/track", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity saveUserTrack(@RequestBody TrackJSON data){
        databaseService.saveTrackOfUser(TrackMapper.mapJsonToObject(data));
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @ResponseBody
    @GetMapping(path = "/missing-users")
    public ResponseEntity getUsersWithUnknownStatus(){
        return ResponseEntity.ok().body(databaseService.getAllMissingUsers());
    }

    @DeleteMapping(path = "/unsubscribe-user", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity deleteUserHistory(@RequestBody TrackTimeJSON phoneWithTime) throws IOException {
        databaseService.unsubscribeUser(phoneWithTime.getNumber(), phoneWithTime.getStartDate(), phoneWithTime.getStopDate());
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping(path = "/all-data")
    public ResponseEntity getAllData(){
        return ResponseEntity.ok().body(databaseService.getAllData());
    }

    @GetMapping(path = "/find-users", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getAllUsers(@RequestBody TrackTimeJSON phoneWithTime){
        return ResponseEntity.ok().body(databaseService.getListOfUsersWhoMetUserRecently(phoneWithTime.getNumber(), phoneWithTime.getStartDate(), phoneWithTime.getStopDate()));
    }

    @GetMapping(path = "/all-user-data", consumes = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity getAllUserData(@RequestBody String phoneNumber){
        return ResponseEntity.ok().body(databaseService.getAllUserData(phoneNumber));
    }

    @GetMapping(path = "/user-locations", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getUserData(@RequestBody TrackTimeJSON phoneWithTime) {
        return ResponseEntity.ok().body(databaseService.getUserLocationsFromTimeInterval(phoneWithTime.getNumber(), phoneWithTime.getStartDate(), phoneWithTime.getStopDate()));
    }

    @GetMapping(path = "/users-time", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getAllUsersFromPlaceAndTimeInterval(@RequestBody TrackTimeJSON phoneWithTime){
        return ResponseEntity.ok().body(databaseService.getAllUsersFromPlaceAndTimeInterval(phoneWithTime.getNumber(), phoneWithTime.getStartDate(), phoneWithTime.getStopDate()));
    }
}
