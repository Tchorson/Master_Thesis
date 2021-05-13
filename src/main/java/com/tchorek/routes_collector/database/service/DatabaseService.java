package com.tchorek.routes_collector.database.service;

import com.tchorek.routes_collector.database.json.RegistrationData;
import com.tchorek.routes_collector.database.json.ServerData;
import com.tchorek.routes_collector.database.model.*;
import com.tchorek.routes_collector.database.repositories.*;
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
    HangoutRegistrationsRepository hangoutRegistrationsRepository;
    HistoryTrackRepository historyTrackRepository;
    FugitiveRepository fugitiveRepository;
    AgentRepository agentRepository;


    @Autowired
    public DatabaseService(DailyTrackRepository dailyTrackRepository, HangoutRegistrationsRepository hangoutRegistrationsRepository, HistoryTrackRepository historyTrackRepository, FugitiveRepository fugitiveRepository, AgentRepository agentRepository) {
        this.dailyTrackRepository = dailyTrackRepository;
        this.hangoutRegistrationsRepository = hangoutRegistrationsRepository;
        this.historyTrackRepository = historyTrackRepository;
        this.fugitiveRepository = fugitiveRepository;
        this.agentRepository = agentRepository;
    }

    @Scheduled(cron = "0 0 * * * *")
    public void transferDailyDataToHistorical() {
        log.info("Transferring daily data to history records");
        historyTrackRepository.transferDailyDataToHistory();
        dailyTrackRepository.deleteAll();
        hangoutRegistrationsRepository.deleteVerifiedRegistrations();
    }

    public String getUserTargetArea(String targetArea){
        return hangoutRegistrationsRepository.getUserRegistration(targetArea);
    }

    public boolean isDeviceInService(String device){
        return agentRepository.isDeviceInDB(device) > 0;
    }

    public void saveAgent(Agent agent){
        agentRepository.save(agent);
    }

    public void saveNewFugitiveInDB(RegistrationData currentUserData) {
        if (fugitiveRepository.findById(currentUserData.getUserData()).isEmpty())
            fugitiveRepository.save(Mapper.mapJsonToFugitive(currentUserData));
    }

    public boolean isApprovalInDB(Registration approval) {
        return hangoutRegistrationsRepository.findById(approval.getPhoneNumber()).isPresent();
    }

    public List<Registration> getAllNewRegistrations() {
        return hangoutRegistrationsRepository.getAllNewRegistrations();
    }

    public Set<String> getAllApprovedUsers() {
        return hangoutRegistrationsRepository.getAllApprovedUsers();
    }

    public List<HistoryTracks> getUserHistory(String user) {
        return historyTrackRepository.getUserHistory(user);
    }

    public void saveTrackOfUser(DailyRecord userDailyRecord) {
        dailyTrackRepository.save(userDailyRecord);
    }

    public void saveUserActivityData(Registration registration) {
        hangoutRegistrationsRepository.save(registration);
    }

    public Iterable<Fugitive> getAllFugitives() {
        return fugitiveRepository.getAllUnreportedFugitives();
    }

    public void markReportedFugitives(Fugitive[] listOfFugitives){
        List<String> phoneNumbers = Arrays.stream(listOfFugitives).map(Fugitive::getPhoneNumber).collect(Collectors.toList());
        fugitiveRepository.markFugitivesAsReported(phoneNumbers);
    }

    public Iterable<Registration> getAllRegisteredUsers() {
        return hangoutRegistrationsRepository.findAll();
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

    public List<Agent> getDevicesFromArea(String targetArea){
        return agentRepository.getDevicesFromSpecificArea(targetArea);
    }
}
