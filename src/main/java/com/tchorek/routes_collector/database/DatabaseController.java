package com.tchorek.routes_collector.database;

import com.tchorek.routes_collector.database.json.RegistrationData;
import com.tchorek.routes_collector.message.json.BluetoothData;
import com.tchorek.routes_collector.database.json.ServerData;
import com.tchorek.routes_collector.database.service.DatabaseService;
import com.tchorek.routes_collector.monitoring.service.MonitoringService;
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

    @Autowired
    MonitoringService monitoringService;

    @PutMapping(path = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity registerUser(@RequestBody RegistrationData data){
        databaseService.saveRegistration(data.getUserData(), data.getRegistrationDate(), data.getLatitude(), data.getLongitude());
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping(path = "/registrations")
    public ResponseEntity getRegistrations(){
        return ResponseEntity.ok().body(databaseService.getAllRegistrations());
    }

    @PostMapping(path = "/user-track", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity saveUserTrack(@RequestBody BluetoothData data){
        databaseService.saveTrackOfUser(TrackMapper.mapJsonToObject(data));
        monitoringService.saveUserActivity(TrackMapper.mapJsonToObject(data));
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @ResponseBody
    @GetMapping(path = "/missing-users")
    public ResponseEntity getUsersWithUnknownStatus(){
        return ResponseEntity.ok().body(monitoringService.getAllMissingUsers());
    }

    @DeleteMapping(path = "/remove-user", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity deleteUserHistory(@RequestBody String phoneNumber) {
        databaseService.unsubscribeUser(phoneNumber);
        monitoringService.removeUserFromActivityList(phoneNumber);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping(path = "/all-data")
    public ResponseEntity getAllData(){
        return ResponseEntity.ok().body(databaseService.getAllData());
    }

    @GetMapping(path = "/find-users", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getUsersWhoMetUserRecently(@RequestBody ServerData phoneWithTime){
        return ResponseEntity.ok().body(databaseService.getUsersWhoMetUserRecently(phoneWithTime.getUserData(), phoneWithTime.getStartDate(), phoneWithTime.getStopDate()));
    }

    @GetMapping(path = "/all-user-data", consumes = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity getUserRoute(@RequestBody String phoneNumber){
        return ResponseEntity.ok().body(databaseService.getUserRoute(phoneNumber));
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
