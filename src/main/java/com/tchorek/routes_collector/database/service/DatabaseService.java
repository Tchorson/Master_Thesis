package com.tchorek.routes_collector.database.service;

import com.tchorek.routes_collector.database.model.Track;
import com.tchorek.routes_collector.database.repositories.TrackRepository;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@NoArgsConstructor
@Service
public class DatabaseService {

    TrackRepository trackRepository;

    Map<String, Long> lastUserActivity = new LinkedHashMap<>();
    Set<String> usersWithUnknownStatus = new LinkedHashSet<>();

    StringBuilder stringBuilder;
    BufferedWriter bufferedWriter;

    private static final byte CLEAR_STRING_BUILDER = 0;
    private static final String SUFFIX = ".txt";
    private static final String SEPARATOR = "_";

    @Autowired
    public DatabaseService(TrackRepository trackRepository) {
        this.trackRepository = trackRepository;
        stringBuilder = new StringBuilder();
        trackRepository.getAllUsersWithLastActivity()
                .forEach(track -> lastUserActivity.put(track.getPhoneNumber(), track.getDate()));
    }

    @Scheduled(cron = "0 0/5 * * * *")
    public void findAllInactiveUsers() {
        long currentTime = Instant.now().getEpochSecond();
        Set<String> newUnknownUsers = lastUserActivity.entrySet().stream()
                .filter(userLastTrack -> currentTime - userLastTrack.getValue() > 360)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        usersWithUnknownStatus.clear();
        usersWithUnknownStatus.addAll(newUnknownUsers);
    }

    public Set<String> getAllMissingUsers() {
        return usersWithUnknownStatus;
    }

    public void saveTrackOfUser(Track userTrack) {
        lastUserActivity.put(userTrack.getPhoneNumber(), userTrack.getDate());
        trackRepository.save(userTrack);
    }

    public void unsubscribeUser(String phoneNumber, long startTimestamp, long stopTimestamp) throws IOException {
        if (trackRepository.findById(phoneNumber).isPresent()) {
            saveUserActivityToFile(phoneNumber, startTimestamp, stopTimestamp);
            removeUserData(phoneNumber);
        }
    }

    private void saveUserActivityToFile(String userNumber, long startTimestamp, long stopTimestamp) throws IOException {
        collectUserDailyActivity(userNumber, startTimestamp, stopTimestamp);
        saveToFile(userNumber);
        stringBuilder.setLength(CLEAR_STRING_BUILDER);
    }

    private void saveToFile(String userNumber) throws IOException {
        long timestamp = Instant.now().getEpochSecond();
        log.info("Saving {} to log file", userNumber);
        bufferedWriter = new BufferedWriter(new FileWriter(new File(
                System.getProperty("user.dir")) + File.separator + userNumber + SEPARATOR + timestamp + SUFFIX));
        bufferedWriter.write(stringBuilder.toString());
        bufferedWriter.flush();
        bufferedWriter.close();
    }

    private void collectUserDailyActivity(String userNumber, long startTimestamp, long stopTimestamp) {
        log.info("Collecting {} daily activity", userNumber);
        List<Track> userDailyRoute = trackRepository.getUserRouteFromParticularTime(userNumber, startTimestamp, stopTimestamp);
        userDailyRoute.forEach(track ->stringBuilder.append(track.toString()));
    }

    private void removeUserFromActivityList(String userNumber) {
        lastUserActivity.remove(userNumber);
    }

    public void removeUserData(String userNumber) {
        removeUserFromActivityList(userNumber);
        trackRepository.deleteUserRoute(userNumber);
    }

    public void clearDatabase() {
        lastUserActivity.clear();
        trackRepository.deleteAll();
    }

    public List<Track> getListOfUsersByLocationAndTime(String location, long timestamp) {
        return trackRepository.getListOfUsersByLocationAndTime(location, timestamp);
    }

    public List<String> getUsersWhoMetUserRecently(String number, long startTime, long stopTime) {
        return trackRepository.getUsersWhoMetUserRecently(number, startTime, stopTime);
    }

    public Iterable<Track> getAllData() {
        return trackRepository.findAll();
    }

    public Iterable<Track> getUserRoute(String phoneNumber) {
        return trackRepository.getUserRoute(phoneNumber);
    }

    public Iterable<Track> getUserRouteFromParticularTime(String phoneNumber, long startDate, long stopDate) {
        return trackRepository.getUserRouteFromParticularTime(phoneNumber, startDate, stopDate);
    }

    public List<String> getAllUsersFromParticularPlaceAndTime(String location, long startDate, long stopDate) {
        return trackRepository.getAllUsersFromParticularPlaceAndTime(location, startDate, stopDate);
    }
}
