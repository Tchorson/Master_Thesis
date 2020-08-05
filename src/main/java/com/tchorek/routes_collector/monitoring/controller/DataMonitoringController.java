package com.tchorek.routes_collector.monitoring.controller;

import com.tchorek.routes_collector.database.json.RegistrationData;
import com.tchorek.routes_collector.database.model.Registration;
import com.tchorek.routes_collector.database.service.DatabaseService;
import com.tchorek.routes_collector.monitoring.service.MonitoringService;
import com.tchorek.routes_collector.utils.Mapper;
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
public class DataMonitoringController {

    @Autowired
    MonitoringService monitoringService;

    @Autowired
    DatabaseService databaseService;

    @ResponseBody
    @PostMapping(path = "/verify", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity verifyUserLocation(@RequestBody RegistrationData verificationData) {
        try {
            if (monitoringService.isUserCurrentLocationValid(verificationData)) return ResponseEntity.ok(HttpStatus.OK);
            else {
                monitoringService.claimUserAsFugitive(verificationData);
                return ResponseEntity.ok().body("USER " + verificationData.getUserData() + " IN UNKNOWN AREA at time " + Instant.ofEpochSecond(verificationData.getDate()));
            }

        } catch (Exception e) {
            log.warn("PLEASE CONTACT SANEPID IMMEDIATELY FOR {}", verificationData.getUserData());
            return ResponseEntity.ok(HttpStatus.UNAUTHORIZED);
        }
    }

    @ResponseBody
    @GetMapping(path = "/missing-users")
    public ResponseEntity getUsersWithUnknownStatus() {
        return ResponseEntity.ok().body(monitoringService.getAllMissingUsers());
    }

    @PutMapping(path = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity registerUser(@RequestBody RegistrationData registration) {
        Registration mappedObj = Mapper.mapJsonToObject(registration);
        databaseService.saveRegistration(mappedObj);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping(path = "/registrations")
    public ResponseEntity getAllRegisteredUsers() {
        return ResponseEntity.ok().body(databaseService.getAllRegisteredUsers());
    }

    @PostMapping(path = "/send-approval-decisions", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity sendAllDecisionsToDB(@RequestBody Iterable<Registration> decisions) {
        decisions.forEach(decision -> {
            if (databaseService.isApprovalInDB(decision)) {
                log.info("APPROVING {}",decision.getPhoneNumber());
                databaseService.saveRegistration(decision);
            }
            else
                log.warn("DENIED APPROVAL FOR UNKNOWN USER {}",decision.getPhoneNumber());
        });
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping(path = "/get-users-without-decision")
    public ResponseEntity getAllNewApprovals() {
        return ResponseEntity.ok().body(databaseService.getAllNewRegistrations());
    }

    @GetMapping(path = "/get-all-approved-users")
    public ResponseEntity getAllApprovedUsersToLeave() {
        return ResponseEntity.ok().body(databaseService.getAllApprovedUsers());
    }

    @GetMapping(path = "/get-all-fugitives")
    public ResponseEntity getAllFugitives() {
        return ResponseEntity.ok().body(databaseService.getAllFugitives());
    }
}
