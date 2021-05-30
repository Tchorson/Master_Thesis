package com.tchorek.routes_collector.database.controller;

import com.tchorek.routes_collector.database.json.ServerData;
import com.tchorek.routes_collector.database.model.DailyRecord;
import com.tchorek.routes_collector.database.model.HistoryTracks;
import com.tchorek.routes_collector.database.model.RegisteredUser;
import com.tchorek.routes_collector.database.model.SickPerson;
import com.tchorek.routes_collector.database.service.DatabaseService;
import com.tchorek.routes_collector.encryption.Encryptor;
import com.tchorek.routes_collector.encryption.EncryptorProperties;
import com.tchorek.routes_collector.message.json.BluetoothData;
import com.tchorek.routes_collector.monitoring.service.DataMonitoringService;
import com.tchorek.routes_collector.monitoring.service.LoginService;
import com.tchorek.routes_collector.utils.Mapper;
import com.tchorek.routes_collector.utils.Timer;
import com.tchorek.routes_collector.utils.Validator;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Log4j2
@Controller
@CrossOrigin
public class UserDataController {

    @Autowired
    DatabaseService databaseService;

    @Autowired
    DataMonitoringService dataMonitoringService;

    @Autowired
    Validator validator;

    @Autowired
    LoginService loginService;

    @Autowired
    EncryptorProperties encryptorProperties;

    @PostMapping(path = "/point", consumes = MediaType.APPLICATION_JSON_VALUE) //from python device
    public ResponseEntity saveUserLocation(@RequestBody BluetoothData data) {
        data.setUser(decryptUser(data.getUser()));
        return analyzeData(data);
    }

    @GetMapping(path = "/daily-data", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getDailyData(@RequestParam(name = "token") String token) {
        if (!loginService.isTokenValid(token)) {
            return ResponseEntity.ok(HttpStatus.FORBIDDEN);
        }

        Iterable<DailyRecord> dailyData = databaseService.getDailyData();
        dailyData.forEach(data -> data.setPhoneNumber(encryptUser(data.getPhoneNumber())));
        return ResponseEntity.ok().body(dailyData);
    }

    @GetMapping(path = "/history-data", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getHistoryData(@RequestParam(name = "token") String token) {
        if (!loginService.isTokenValid(token)) {
            return ResponseEntity.ok(HttpStatus.FORBIDDEN);
        }

        Iterable<HistoryTracks> historyData = databaseService.getHistoryData();
        historyData.forEach(data -> data.setPhoneNumber(encryptUser(data.getPhoneNumber())));
        return ResponseEntity.ok().body(historyData);
    }

    @PostMapping(path = "/find-users", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getUsersWhoMetUserRecently(@RequestBody ServerData phoneWithTime, @RequestParam(name = "token") String token) {
        if (!loginService.isTokenValid(token)) {
            return ResponseEntity.ok(HttpStatus.FORBIDDEN);
        }
        phoneWithTime.setUserData(decryptUser(phoneWithTime.getUserData()));
        Set<String> users = databaseService.getUsersWhoMetUser(phoneWithTime);
        Set<String> encryptedUsers = new LinkedHashSet<>();
        users.forEach(user -> encryptedUsers.add(encryptUser(user)));
        return ResponseEntity.ok().body(encryptedUsers);
    }

    @GetMapping(path = "/user-daily-route", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getUserRoute(@RequestBody String phoneNumber, @RequestParam(name = "token") String token) {
        if (!loginService.isTokenValid(token)) {
            return ResponseEntity.ok(HttpStatus.FORBIDDEN);
        }
        Iterable<DailyRecord> records = databaseService.getUserDailyRoute(decryptUser(phoneNumber));
        records.forEach(record -> record.setPhoneNumber(encryptUser(record.getPhoneNumber())));
        return ResponseEntity.ok().body(records);
    }

    @GetMapping(path = "/user-history", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getUserHistory(@RequestBody String phoneNumber, @RequestParam(name = "token") String token) {
        if (!loginService.isTokenValid(token)) {
            return ResponseEntity.ok(HttpStatus.FORBIDDEN);
        }
        List<HistoryTracks> userPathsHistory = databaseService.getUserHistory(decryptUser(phoneNumber));
        userPathsHistory.forEach(user -> user.setPhoneNumber(encryptUser(user.getPhoneNumber())));
        return ResponseEntity.ok().body(userPathsHistory);
    }

    @PostMapping(path = "/users-data", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getAllUsersFromParticularPlaceAndTime(@RequestBody ServerData locationAndTime, @RequestParam(name = "token") String token) {
        if (!loginService.isTokenValid(token)) {
            return ResponseEntity.ok(HttpStatus.FORBIDDEN);
        }
        Set<String> users = databaseService.getAllUsersFromParticularPlaceAndTime(decryptUser(locationAndTime.getUserData()), locationAndTime.getStartDate(), locationAndTime.getStopDate());
        Set<String> encryptedUsers = new LinkedHashSet<>();
        users.forEach(user -> encryptedUsers.add(encryptUser(user)));
        return ResponseEntity.ok().body(encryptedUsers);
    }

    @GetMapping(path = "/sick-list", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getListOfSickPeople(@RequestParam(name = "token") String token) {
        if (!loginService.isTokenValid(token)) {
            return ResponseEntity.ok(HttpStatus.FORBIDDEN);
        }
        List<SickPerson> sickPeopleList = dataMonitoringService.getSickPeopleList();
        sickPeopleList.forEach(user -> user.setPhoneNumber(encryptUser(user.getPhoneNumber())));
        return ResponseEntity.ok().body(sickPeopleList);
    }

    @PostMapping(path = "/report-sick-people", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity reportSickPeople(@RequestBody List<String> ids, @RequestParam(name = "token") String token) {
        if (!loginService.isTokenValid(token))
            return ResponseEntity.ok(HttpStatus.FORBIDDEN);
        dataMonitoringService.reportSickPeople(ids);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PostMapping(path = "/add-sick", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity addSickPerson(@RequestBody SickPerson newPerson, @RequestParam(name = "token") String token) {
        if (!loginService.isTokenValid(token) && !loginService.isRegistered(newPerson.getPhoneNumber()))
            return ResponseEntity.ok(HttpStatus.FORBIDDEN);
        dataMonitoringService.addSickPerson(newPerson);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PostMapping(path = "/delete-user", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity deleteUser(@RequestBody RegisteredUser userToDelete) {
        if (!loginService.isTokenValid(userToDelete.getToken())) {
            return ResponseEntity.ok(HttpStatus.FORBIDDEN);
        }
        dataMonitoringService.deleteUser(userToDelete);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PostMapping(path = "/register-user", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity registerUser(@RequestBody RegisteredUser registeredUser) {
        dataMonitoringService.registerUser(registeredUser);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    private ResponseEntity analyzeData(BluetoothData data) {
        String deviceName = data.getDeviceName();
        String user = data.getUser();
        String targetPlace = databaseService.getUserTargetArea(user);
        if(targetPlace == null){
            log.warn("UNKNOWN USER {} ", user);
            return ResponseEntity.ok(HttpStatus.UNAUTHORIZED);
        }

        if(!deviceName.contains(targetPlace)){
            log.warn("USER {} IS IN DIFFERENT AREA {} THAN DECLARED {} ", user, deviceName, targetPlace);
            dataMonitoringService.addNewFugitive(user);
            return ResponseEntity.ok(HttpStatus.UNAUTHORIZED);
        }

        if (validator.isDeviceValid(deviceName) && dataMonitoringService.checkIfUserIsRegistered(user)) {
            if (dataMonitoringService.isUserBeforeTime(user)) {
                log.warn("USER {} ARRIVED BEFORE SCHEDULED TIME: {}", user, Timer.getCurrentTimeInSeconds());
            }
            Map<Long,Boolean> map = dataMonitoringService.isUserAfterTime(user);

            if (map.values().stream().findFirst().get()) {
                log.warn("USER {} IS AFTER DECLARED TIME OF RETURN. ESTIMATED TIME: {}", user,
                        map.keySet().stream().findFirst().get());
                dataMonitoringService.addNewFugitive(user);
                return ResponseEntity.ok(HttpStatus.UNAUTHORIZED);
            }

            databaseService.saveTrackOfUser(Mapper.mapJsonToObject(data));
            dataMonitoringService.saveUserActivity(Mapper.mapJsonToObject(data));
            log.debug("USER {} REACHED {} DEVICE", user, Timer.getCurrentTimeInSeconds());
            return ResponseEntity.ok(HttpStatus.OK);
        }
        if (validator.isDeviceValid(deviceName)) {
            log.warn("UNAUTHORIZED USER IN THE AREA: {} at time {}", user, Timer.getFullDate(Timer.getCurrentTimeInSeconds()));
        } else {
            log.warn("UNKNOWN DEVICE ATTEMPT  {} at time {}", deviceName, Timer.getFullDate(Timer.getCurrentTimeInSeconds()));
        }
        return ResponseEntity.ok(HttpStatus.UNAUTHORIZED);
    }

    private String encryptUser(String data) {
        return Encryptor.encrypt(data, encryptorProperties.getKey(), encryptorProperties.getIv());
    }

    private String decryptUser(String data) {
        return Encryptor.decrypt(data, encryptorProperties.getKey(), encryptorProperties.getIv());
    }
}
