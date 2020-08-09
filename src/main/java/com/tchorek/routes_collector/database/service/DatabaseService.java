package com.tchorek.routes_collector.database.service;

import com.tchorek.routes_collector.database.json.RegistrationData;
import com.tchorek.routes_collector.database.model.DailyRecord;
import com.tchorek.routes_collector.database.model.Fugitive;
import com.tchorek.routes_collector.database.model.HistoryTracks;
import com.tchorek.routes_collector.database.model.Registration;
import com.tchorek.routes_collector.database.repositories.DailyTrackRepository;
import com.tchorek.routes_collector.database.repositories.FugitiveRepository;
import com.tchorek.routes_collector.database.repositories.HistoryTrackRepository;
import com.tchorek.routes_collector.database.repositories.RegistrationRepository;
import com.tchorek.routes_collector.utils.Mapper;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.management.openmbean.KeyAlreadyExistsException;
import java.util.List;
import java.util.Set;

@Log4j2
@NoArgsConstructor
@Service
public class DatabaseService {

    DailyTrackRepository dailyTrackRepository;
    RegistrationRepository registrationRepository;
    HistoryTrackRepository historyTrackRepository;
    FugitiveRepository fugitiveRepository;

    @Autowired
    public DatabaseService(DailyTrackRepository dailyTrackRepository, RegistrationRepository registrationRepository, HistoryTrackRepository historyTrackRepository, FugitiveRepository fugitiveRepository) {
        this.dailyTrackRepository = dailyTrackRepository;
        this.registrationRepository = registrationRepository;
        this.historyTrackRepository = historyTrackRepository;
        this.fugitiveRepository = fugitiveRepository;
    }

    @Scheduled(cron = "0 0 * * * *")
    public void transferHistoricalDataAndClearDatabase() {
        log.info("Transferring daily data to history records");
        historyTrackRepository.transferDailyDataToHistory();
        dailyTrackRepository.deleteUsers();
        registrationRepository.deleteRegistrations();
    }

    public void saveNewFugitiveInDB(RegistrationData currentUserData) {
        if (fugitiveRepository.findById(currentUserData.getUserData()).isPresent())
            throw new KeyAlreadyExistsException("Fugitive is already in system");
        fugitiveRepository.save(Mapper.mapJsonToFugitive(currentUserData));
    }

    public boolean isApprovalInDB(Registration approval){
        return registrationRepository.findById(approval.getPhoneNumber()).isPresent();
    }

    public void saveAllRegistrations(Iterable<Registration> decisions){
        registrationRepository.saveAll(decisions);
    }

    public List<Registration> getAllNewRegistrations(){
        return registrationRepository.getAllNewRegistrations();
    }

    public Set<String> getAllApprovedUsers(){
       return registrationRepository.getAllApprovedUsers();
    }

    public List<HistoryTracks> getUserHistory(String user){
        return historyTrackRepository.getUserHistory(user);
    }

    public void saveTrackOfUser(DailyRecord userDailyRecord) {
        dailyTrackRepository.save(userDailyRecord);
    }

    public void saveRegistration(Registration registration){
        registrationRepository.save(registration);
    }

    public Iterable<Fugitive> getAllFugitives(){
        return fugitiveRepository.findAll();
    }

    public Iterable<Registration> getAllRegisteredUsers(){
       return registrationRepository.findAll();
    }

    public List<DailyRecord> getListOfUsersByLocationAndTime(String location, long timestamp) {
        return dailyTrackRepository.getListOfUsersByLocationAndTime(location, timestamp);
    }

    public List<String> getUsersWhoMetUserRecently(String number, long startTime, long stopTime) {
        return dailyTrackRepository.getUsersWhoMetUserRecently(number, startTime, stopTime);
    }

    public Iterable<DailyRecord> getAllData() {
        return dailyTrackRepository.findAll();
    }

    public Iterable<DailyRecord> getUserRoute(String phoneNumber) {
        return dailyTrackRepository.getUserRoute(phoneNumber);
    }

    public Iterable<DailyRecord> getUserRouteFromParticularTime(String phoneNumber, long startDate, long stopDate) {
        return dailyTrackRepository.getUserRouteFromParticularTime(phoneNumber, startDate, stopDate);
    }

    public List<String> getAllUsersFromParticularPlaceAndTime(String location, long startDate, long stopDate) {
        return dailyTrackRepository.getAllUsersFromParticularPlaceAndTime(location, startDate, stopDate);
    }
}
