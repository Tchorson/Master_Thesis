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

import java.util.LinkedHashSet;
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

    private final long TWO_WEEKS_PERIOD = 1468800;

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
        if (!fugitiveRepository.findById(currentUserData.getUserData()).isPresent())
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
        return fugitiveRepository.findAll();
    }

    public Iterable<Registration> getAllRegisteredUsers() {
        return registrationRepository.findAll();
    }

    public Set<String> getUsersWhoMetUser(ServerData userData) {
        return getAllRecords(getUsersWhoMetUserRecently(userData.getUserData(), userData.getStartDate(), userData.getStopDate()),
                getUsersWhoMetUserInPast(userData.getUserData(), userData.getStartDate() - TWO_WEEKS_PERIOD, userData.getStartDate()));
    }

    public List<String> getUsersWhoMetUserRecently(String number, long startTime, long stopTime) {
        return dailyTrackRepository.getUsersWhoMetUserRecently(number, startTime, stopTime);
    }

    public List<String> getUsersWhoMetUserInPast(String number, long startTime, long stopTime) {
        return historyTrackRepository.getUsersWhoMetUser(number, startTime, stopTime);
    }

    public Iterable<DailyRecord> getDailyData() {
        return dailyTrackRepository.findAll();
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
