package com.tchorek.routes_collector.controllers;

import com.tchorek.routes_collector.database.json.RegistrationData;
import com.tchorek.routes_collector.database.model.Registration;
import com.tchorek.routes_collector.message.json.BluetoothData;
import com.tchorek.routes_collector.database.json.ServerData;
import com.tchorek.routes_collector.database.service.DatabaseService;
import com.tchorek.routes_collector.monitoring.service.MonitoringService;
import com.tchorek.routes_collector.utils.Mapper;
import com.tchorek.routes_collector.utils.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class DatabaseController {

    @Autowired
    DatabaseService databaseService;

    @Autowired
    MonitoringService monitoringService;


    @PutMapping(path = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity registerUser(@RequestBody RegistrationData registration){
        databaseService.saveRegistration(registration.getUserData(), registration.getDate(), registration.getLatitude(), registration.getLongitude());
        monitoringService.approveUser(Mapper.mapJsonToObject(registration));
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping(path = "/registrations")
    public ResponseEntity getAllRegisteredUsers(){
        return ResponseEntity.ok().body(databaseService.getAllRegisteredUsers());
    }

    @PostMapping(path = "/send-approval-decisions", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity sendAllDecisionsToDB(@RequestBody List<Registration> decisions){
        databaseService.saveAllRegistrations(decisions);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping(path = "/get-all-approvals")
    public ResponseEntity getAllApprovals(){
        return ResponseEntity.ok().body(databaseService.getAllApprovals());
    }

    @GetMapping(path = "/get-all-new-approvals")
    public ResponseEntity getAllNewApprovals(){
        return ResponseEntity.ok().body(databaseService.getAllNewRegistrations());
    }

    @GetMapping(path = "/get-all-approved-users")
    public ResponseEntity getAllApprovedUsersToLeave(){
        return ResponseEntity.ok().body(databaseService.getAllApprovedUsers());
    }

    @PostMapping(path = "/save-user-track", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity saveUserTrack(@RequestBody BluetoothData data){
        if(Validator.DeviceValidator.isDeviceValid(data.getLocation()) && monitoringService.checkIfUserIsRegisteredAndEligibleForWalk(data.getUser())){
            databaseService.saveTrackOfUser(Mapper.mapJsonToObject(data));
            monitoringService.saveUserActivity(Mapper.mapJsonToObject(data));
            return ResponseEntity.ok(HttpStatus.OK);
        }
        return ResponseEntity.ok(HttpStatus.UNAUTHORIZED);
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
