package com.tchorek.routes_collector.database;

import com.tchorek.routes_collector.database.json.BluetoothDataJSON;
import com.tchorek.routes_collector.database.json.ServerDataJSON;
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

    @PostMapping(path = "/user-track", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity saveUserTrack(@RequestBody BluetoothDataJSON data){
        databaseService.saveTrackOfUser(TrackMapper.mapJsonToObject(data));
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @ResponseBody
    @GetMapping(path = "/missing-users")
    public ResponseEntity getUsersWithUnknownStatus(){
        return ResponseEntity.ok().body(databaseService.getAllMissingUsers());
    }

    @DeleteMapping(path = "/remove-user", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity deleteUserHistory(@RequestBody ServerDataJSON phoneWithTime) throws IOException {
        databaseService.unsubscribeUser(phoneWithTime.getUserData(), phoneWithTime.getStartDate(), phoneWithTime.getStopDate());
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping(path = "/all-data")
    public ResponseEntity getAllData(){
        return ResponseEntity.ok().body(databaseService.getAllData());
    }

    @GetMapping(path = "/find-users", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getUsersWhoMetUserRecently(@RequestBody ServerDataJSON phoneWithTime){
        return ResponseEntity.ok().body(databaseService.getUsersWhoMetUserRecently(phoneWithTime.getUserData(), phoneWithTime.getStartDate(), phoneWithTime.getStopDate()));
    }

    @GetMapping(path = "/all-user-data", consumes = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity getUserRoute(@RequestBody String phoneNumber){
        return ResponseEntity.ok().body(databaseService.getUserRoute(phoneNumber));
    }

    @GetMapping(path = "/user-route", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getUserRouteFromParticularTime(@RequestBody ServerDataJSON phoneWithTime) {
        return ResponseEntity.ok().body(databaseService.getUserRouteFromParticularTime(phoneWithTime.getUserData(), phoneWithTime.getStartDate(), phoneWithTime.getStopDate()));
    }

    @GetMapping(path = "/users-time", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getAllUsersFromParticularPlaceAndTime(@RequestBody ServerDataJSON locationAndTime){
        return ResponseEntity.ok().body(databaseService.getAllUsersFromParticularPlaceAndTime(locationAndTime.getUserData(), locationAndTime.getStartDate(), locationAndTime.getStopDate()));
    }
}
