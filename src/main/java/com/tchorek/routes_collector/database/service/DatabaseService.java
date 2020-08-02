package com.tchorek.routes_collector.database.service;

import com.tchorek.routes_collector.database.model.Registration;
import com.tchorek.routes_collector.database.model.DailyTracks;
import com.tchorek.routes_collector.database.repositories.RegistrationRepository;
import com.tchorek.routes_collector.database.repositories.DailyTrackRepository;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Log4j2
@NoArgsConstructor
@Service
public class DatabaseService {

    DailyTrackRepository dailyTrackRepository;
    RegistrationRepository registrationRepository;


    @Autowired
    public DatabaseService(DailyTrackRepository dailyTrackRepository) {
        this.dailyTrackRepository = dailyTrackRepository;
    }

    public void saveTrackOfUser(DailyTracks userDailyTracks) {
        dailyTrackRepository.save(userDailyTracks);
    }

    public void unsubscribeUser(String phoneNumber){
        if (dailyTrackRepository.findById(phoneNumber).isPresent()) {
            removeUserRoute(phoneNumber);
        }
    }

    public void saveRegistration(String userPhone, long registrationDate, String lat, String lng){
        registrationRepository.save(new Registration(userPhone, registrationDate, lat, lng));
    }

    public Iterable<Registration> getAllRegistrations(){
       return registrationRepository.findAll();
    }

    public void removeUserRoute(String userNumber) {
        dailyTrackRepository.deleteUserRoute(userNumber);
    }

    public void clearDatabase() {
        dailyTrackRepository.deleteAll();
    }

    public List<DailyTracks> getListOfUsersByLocationAndTime(String location, long timestamp) {
        return dailyTrackRepository.getListOfUsersByLocationAndTime(location, timestamp);
    }

    public List<String> getUsersWhoMetUserRecently(String number, long startTime, long stopTime) {
        return dailyTrackRepository.getUsersWhoMetUserRecently(number, startTime, stopTime);
    }

    public Iterable<DailyTracks> getAllData() {
        return dailyTrackRepository.findAll();
    }

    public Iterable<DailyTracks> getUserRoute(String phoneNumber) {
        return dailyTrackRepository.getUserRoute(phoneNumber);
    }

    public Iterable<DailyTracks> getUserRouteFromParticularTime(String phoneNumber, long startDate, long stopDate) {
        return dailyTrackRepository.getUserRouteFromParticularTime(phoneNumber, startDate, stopDate);
    }

    public List<String> getAllUsersFromParticularPlaceAndTime(String location, long startDate, long stopDate) {
        return dailyTrackRepository.getAllUsersFromParticularPlaceAndTime(location, startDate, stopDate);
    }
}
