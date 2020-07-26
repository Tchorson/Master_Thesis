package com.tchorek.routes_collector.database;

import com.tchorek.routes_collector.database.model.Track;
import com.tchorek.routes_collector.database.model.TrackJSON;
import com.tchorek.routes_collector.database.service.DatabaseService;
import com.tchorek.routes_collector.utils.TrackMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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

    @DeleteMapping(path = "/delete-user-history", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity deleteUserHistory(@RequestBody String phoneNumber){
        databaseService.removeAllUserHistory(phoneNumber);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping(path = "/delete-track", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity deleteTrack(@RequestBody Track trackData){
        databaseService.removeSingleTrack(trackData);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping(path = "/all-data")
    public ResponseEntity getAllData(){
        return ResponseEntity.ok().body(databaseService.getAllData());
    }

    @GetMapping(path = "/all-user-data")
    public ResponseEntity getAllUserData(@RequestBody String phoneNumber){
        return ResponseEntity.ok().body(databaseService.getAllUserData(phoneNumber));
    }

    @GetMapping(path = "/user-locations")
    public ResponseEntity getUserData(@RequestBody String phoneNumber, @RequestParam long startDate, @RequestParam long stopDate) {
        return ResponseEntity.ok().body(databaseService.getUserLocationsFromTimeInterval(phoneNumber, startDate, stopDate));
    }

    @GetMapping(path = "/users-time")
    public ResponseEntity getAllUsersFromPlaceAndTimeInterval(@RequestBody String location, @RequestParam long startDate, @RequestParam long stopDate){
        return ResponseEntity.ok().body(databaseService.getAllUsersFromPlaceAndTimeInterval(location, startDate, stopDate));
    }
}
