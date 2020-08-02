package com.tchorek.routes_collector.database.service;

import com.tchorek.routes_collector.database.model.DailyTracks;
import com.tchorek.routes_collector.database.model.Registration;
import com.tchorek.routes_collector.database.repositories.DailyTrackRepository;
import com.tchorek.routes_collector.database.repositories.HistoryTrackRepository;
import com.tchorek.routes_collector.database.repositories.RegistrationRepository;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Log4j2
@NoArgsConstructor
@Service
public class DatabaseService {

    DailyTrackRepository dailyTrackRepository;
    RegistrationRepository registrationRepository;
    HistoryTrackRepository historyTrackRepository;

    @Autowired
    public DatabaseService(DailyTrackRepository dailyTrackRepository, RegistrationRepository registrationRepository, HistoryTrackRepository historyTrackRepository) {
        this.dailyTrackRepository = dailyTrackRepository;
        this.registrationRepository = registrationRepository;
        this.historyTrackRepository = historyTrackRepository;
    }

    @Scheduled(cron = "0 0 * * * *")
    public void transferHistoricalDataAndClearDatabase() {
        historyTrackRepository.transferDailyDataToHistory();
        dailyTrackRepository.deleteAll();
        registrationRepository.deleteAll();
    }

    public void saveAllRegistrations(List<Registration> decisions){
        registrationRepository.saveAll(decisions);
    }

    public Iterable<Registration> getAllApprovals(){
        return registrationRepository.findAll();
    }

    public List<Registration> getAllNewRegistrations(){
        return registrationRepository.getAllNewRegistrations();
    }

    public Set<String> getAllApprovedUsers(){
       return registrationRepository.getAllApprovedUsers();
    }

    public void saveTrackOfUser(DailyTracks userDailyTracks) {
        dailyTrackRepository.save(userDailyTracks);
    }

    public void saveRegistration(String userPhone, long registrationDate, long lat, long lng){
        registrationRepository.save(new Registration(userPhone, registrationDate, lat, lng, null));
    }

    public Iterable<Registration> getAllRegisteredUsers(){
       return registrationRepository.findAll();
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
