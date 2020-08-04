package com.tchorek.routes_collector.controllers;

import com.tchorek.routes_collector.database.json.RegistrationData;
import com.tchorek.routes_collector.database.model.Registration;
import com.tchorek.routes_collector.database.service.DatabaseService;
import com.tchorek.routes_collector.monitoring.service.MonitoringService;
import com.tchorek.routes_collector.utils.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class RegistrationController {

    @Autowired
    MonitoringService monitoringService;

    @Autowired
    DatabaseService databaseService;

    @PostMapping(path = "/verify", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity verifyUserLocation(@RequestBody RegistrationData verificationData){
        try {
            if (monitoringService.isUserCurrentLocationValid(verificationData)) return ResponseEntity.ok(HttpStatus.OK);
            else
            {
                monitoringService.claimUserAsFugitive(verificationData);
                return ResponseEntity.ok().body("USER NOT AT HOME, PLEASE CONTACT SANEPID IMMEDIATELY");
            }

        } catch (Exception e) {
            return ResponseEntity.ok(HttpStatus.UNAUTHORIZED);
        }
    }

    @ResponseBody
    @GetMapping(path = "/missing-users")
    public ResponseEntity getUsersWithUnknownStatus(){
        return ResponseEntity.ok().body(monitoringService.getAllMissingUsers());
    }

    @PutMapping(path = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity registerUser(@RequestBody RegistrationData registration){
        Registration mappedObj = Mapper.mapJsonToObject(registration);
        databaseService.saveRegistration(mappedObj);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping(path = "/registrations")
    public ResponseEntity getAllRegisteredUsers(){
        return ResponseEntity.ok().body(databaseService.getAllRegisteredUsers());
    }

    @PostMapping(path = "/send-approval-decisions", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity sendAllDecisionsToDB(@RequestBody List<Registration> decisions){
        databaseService.saveAllRegistrations(decisions);
        monitoringService.approveUsers(decisions);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping(path = "/get-all-approvals")
    public ResponseEntity getAllApprovals(){
        return ResponseEntity.ok().body(databaseService.getAllApprovals());
    }

    @GetMapping(path = "/get-users-without-decision")
    public ResponseEntity getAllNewApprovals(){
        return ResponseEntity.ok().body(databaseService.getAllNewRegistrations());
    }

    @GetMapping(path = "/get-all-approved-users")
    public ResponseEntity getAllApprovedUsersToLeave() {
        return ResponseEntity.ok().body(databaseService.getAllApprovedUsers());
    }

    @GetMapping(path = "/get-all-unknown-users")
    public ResponseEntity getAllUnknownUsers() {
        return ResponseEntity.ok().body(monitoringService.getAllUnknownUsers());
    }

    @GetMapping(path = "/get-all-fugitives")
    public ResponseEntity getAllFugitives() {
        return ResponseEntity.ok().body(databaseService.getAllFugitives());
    }
}
