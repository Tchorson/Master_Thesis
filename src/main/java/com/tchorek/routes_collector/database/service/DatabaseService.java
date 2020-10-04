package com.tchorek.routes_collector.database.service;

import com.tchorek.routes_collector.database.json.RegistrationData;
import com.tchorek.routes_collector.database.json.ServerData;
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

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    public void transferDailyDataToHistorical() {
        log.info("Transferring daily data to history records");
        historyTrackRepository.transferDailyDataToHistory();
        dailyTrackRepository.deleteAll();
        registrationRepository.deleteVerifiedRegistrations();
    }

    public void saveNewFugitiveInDB(RegistrationData currentUserData) {
        if (fugitiveRepository.findById(currentUserData.getUserData()).isEmpty())
            fugitiveRepository.save(Mapper.mapJsonToFugitive(currentUserData));
    }

    public boolean isApprovalInDB(Registration approval) {
        return registrationRepository.findById(approval.getPhoneNumber()).isPresent();
    }

    public List<Registration> getAllNewRegistrations() {
        return registrationRepository.getAllNewRegistrations();
    }

    public Set<String> getAllApprovedUsers() {
        return registrationRepository.getAllApprovedUsers();
    }

    public List<HistoryTracks> getUserHistory(String user) {
        return historyTrackRepository.getUserHistory(user);
    }

    public void saveTrackOfUser(DailyRecord userDailyRecord) {
        dailyTrackRepository.save(userDailyRecord);
    }

    public void saveRegistration(Registration registration) {
        registrationRepository.save(registration);
    }

    public Iterable<Fugitive> getAllFugitives() {
        return fugitiveRepository.getAllUnreportedFugitives();
    }

    public void markReportedFugitives(Fugitive[] listOfFugitives){
        List<String> phoneNumbers = Arrays.stream(listOfFugitives).map(Fugitive::getPhoneNumber).collect(Collectors.toList());
        fugitiveRepository.markFugitivesAsReported(phoneNumbers);
    }

    public Iterable<Registration> getAllRegisteredUsers() {
        return registrationRepository.findAll();
    }

    public Set<String> getUsersWhoMetUser(ServerData userData) {
        return getAllRecords(getUsersWhoMetUserRecently(userData.getUserData(), userData.getStartDate(), userData.getStopDate()),
                getUsersWhoMetUserInPast(userData.getUserData(), userData.getStartDate(), userData.getStopDate()));
    }

    public List<String> getUsersWhoMetUserRecently(String number, long startTime, long stopTime) {
        List<String> result = dailyTrackRepository.getUsersWhoMetUserRecently(number, startTime, stopTime);
        return result;
    }

    public List<String> getUsersWhoMetUserInPast(String number, long startTime, long stopTime) {
        List<String> result = historyTrackRepository.getUsersWhoMetUser(number, startTime, stopTime);
        return result;
    }

    public Iterable<DailyRecord> getDailyData() {
        return dailyTrackRepository.findAll();
    }

    public Iterable<HistoryTracks> getHistoryData() {
        return historyTrackRepository.findAll();
    }

    public Iterable<DailyRecord> getUserDailyRoute(String phoneNumber) {
        return dailyTrackRepository.getUserDailyRoute(phoneNumber);
    }

    public Set<String> getAllUsersFromParticularPlaceAndTime(String location, long startDate, long stopDate) {
        return getAllRecords(dailyTrackRepository.getAllUsersFromParticularPlaceAndTime(location, startDate, stopDate),
                historyTrackRepository.getAllUsersFromParticularPlaceAndTime(location, startDate, stopDate));
    }

    private Set<String> getAllRecords(List<String> dailyData, List<String> historicalData) {
        Set<String> users = new LinkedHashSet<>();
        users.addAll(dailyData);
        users.addAll(historicalData);
        return users;
    }
}
