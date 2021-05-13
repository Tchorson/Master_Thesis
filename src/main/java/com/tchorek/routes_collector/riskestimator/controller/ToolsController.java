package com.tchorek.routes_collector.riskestimator.controller;

import com.tchorek.routes_collector.database.json.ServerData;
import com.tchorek.routes_collector.encryption.Encryptor;
import com.tchorek.routes_collector.encryption.EncryptorProperties;
import com.tchorek.routes_collector.message.service.MessageService;
import com.tchorek.routes_collector.riskestimator.model.FuzzyModel;
import com.tchorek.routes_collector.riskestimator.service.RiskEstimatorService;
import com.tchorek.routes_collector.monitoring.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashSet;
import java.util.Set;

@RequestMapping(value = "/service")
@Controller
@CrossOrigin
public class ToolsController {

    @Autowired
    private EncryptorProperties encryptorProperties;

    @Autowired
    private RiskEstimatorService riskEstimatorService;

    @Autowired
    private LoginService loginService;

    @Autowired
    private MessageService messageService;

    @PostMapping(path = "/covid-detection", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity findAllRelatedPeople(@RequestBody ServerData data, @RequestParam(name = "token") String token) {
        if (!loginService.isTokenValid(token)) {
            return ResponseEntity.ok(HttpStatus.FORBIDDEN);
        }
        Set<FuzzyModel> possiblyInfected = riskEstimatorService.findEndangeredPeople(
                decryptUser(data.getUserData()), data.getStartDate(), data.getStopDate());
        Set<FuzzyModel> encryptedUsersData = new LinkedHashSet<>();
        possiblyInfected.forEach(user -> encryptedUsersData.add(encryptUser(user)));
        return ResponseEntity.ok().body(encryptedUsersData);
    }

    @PostMapping(path = "/alert", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity alertRelatedPeople(@RequestBody ServerData data, @RequestParam(name = "token") String token) {
        if (!loginService.isTokenValid(token)) {
            return ResponseEntity.ok(HttpStatus.FORBIDDEN);
        }
        Set<FuzzyModel> possiblyInfected = riskEstimatorService.findEndangeredPeople(decryptUser(data.getUserData()), data.getStartDate(), data.getStopDate());
        messageService.prepareAndSendEmail("List of people containing risk of being infected", possiblyInfected.toString());

        return ResponseEntity.ok(HttpStatus.OK);
    }

    private FuzzyModel encryptUser(FuzzyModel data) {
        data.setUser(encryptUser(data.getUser()));
        return data;
    }

    private String encryptUser(String data) {
        return Encryptor.encrypt(data, encryptorProperties.getKey(), encryptorProperties.getIv());
    }

    private String decryptUser(String data) {
        return Encryptor.decrypt(data, encryptorProperties.getKey(), encryptorProperties.getIv());
    }
}
