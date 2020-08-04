package com.tchorek.routes_collector.controllers;

import com.tchorek.routes_collector.database.json.RegistrationData;
import com.tchorek.routes_collector.monitoring.service.MonitoringService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MonitoringController {

    @Autowired
    MonitoringService monitoringService;

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


}
