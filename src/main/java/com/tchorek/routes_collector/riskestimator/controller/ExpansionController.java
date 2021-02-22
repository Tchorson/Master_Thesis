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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Set;

@RequestMapping(value = "/service")
@Controller
public class ExpansionController {

    @Autowired
    private EncryptorProperties encryptorProperties;

    @Autowired
    private RiskEstimatorService riskEstimatorService;

    @Autowired
    private LoginService loginService;

    @Autowired
    private MessageService messageService;

    @GetMapping(path = "/warn", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity alertAllMetPeople(@RequestBody ServerData data, @RequestParam(name = "token") String token) {
        if (!loginService.isTokenValid(token)) {
            return ResponseEntity.ok(HttpStatus.FORBIDDEN);
        }
        decryptUser(data.getUserData())
        Set<FuzzyModel> suspiciousPeople = riskEstimatorService.findEndangeredPeople(data.getUserData(), data.getStartDate(), data.getStopDate());
        System.out.println(suspiciousPeople.toString());
        messageService.prepareAndSendEmail("List of people containing risk of being infected", suspiciousPeople.toString());
        return ResponseEntity.ok(HttpStatus.ACCEPTED);
    }

    private String encryptUser(String data) {
        return Encryptor.encrypt(data, encryptorProperties.getKey(), encryptorProperties.getIv());
    }

    private String decryptUser(String data) {
        return Encryptor.decrypt(data, encryptorProperties.getKey(), encryptorProperties.getIv());
    }
}
