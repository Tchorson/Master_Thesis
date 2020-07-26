package com.tchorek.routes_collector.database.service;

import com.tchorek.routes_collector.database.model.Track;
import com.tchorek.routes_collector.database.repositories.TrackRepository;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;


@NoArgsConstructor
@Service
public class DatabaseService {

    TrackRepository trackRepository;

    Map<String, Long> lastUserActivity = new LinkedHashMap<>();
    Set<String> usersWithUnknownStatus = new LinkedHashSet<>();

    @Autowired
    public DatabaseService(TrackRepository trackRepository) {
        this.trackRepository = trackRepository;
        trackRepository.getAllUsersWithLastActivityTime()
                .forEach(track -> lastUserActivity.put(track.getPhoneNumber(), track.getDate()));
    }

    @Scheduled(cron = "0 0/5 * * * *")
    public void checkUsersActivity() {
        System.out.println("CHECKING USERS ACTIVITY AT TIME: "+Instant.now().toString());
        long currentTime = Instant.now().getEpochSecond();
        Set<String> newUnknownUsers = lastUserActivity.entrySet().stream()
                .filter(userLastTrack -> currentTime - userLastTrack.getValue() > 360)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        usersWithUnknownStatus.clear();
        usersWithUnknownStatus.addAll(newUnknownUsers);
        newUnknownUsers.forEach(System.out::println);
    }

    public Set<String> getAllMissingUsers() {
        return usersWithUnknownStatus;
    }

    public void saveTrackOfUser(Track userTrack) {
        lastUserActivity.put(userTrack.getPhoneNumber(), userTrack.getDate());
        trackRepository.save(userTrack);
    }

    public void removeSingleTrack(Track userTrack) {
        trackRepository.delete(userTrack);
    }

    public void removeUserFromActivityList(String userNumber) {
        lastUserActivity.remove(userNumber);
    }

    public void removeAllUserHistory(String userNumber) {
        removeUserFromActivityList(userNumber);
        trackRepository.deleteUserHistory(userNumber);
    }

    public void clearDatabase() {
        lastUserActivity.clear();
        trackRepository.deleteAll();
    }

    public List<String> getListOfUsersByLocationAndTime(String location, long timestamp) {
        return trackRepository.getListOfUsersByLocationAndTime(location, timestamp);
    }

    public List<String> getListOfUsersWhoMetUserRecently(String number, long startTime, long stopTime) {
        return trackRepository.getListOfUsersWhoMetUserRecently(number, startTime, stopTime);
    }

    public Iterable<Track> getAllData() {
        return trackRepository.findAll();
    }

    public Iterable<Track> getAllUserData(String phoneNumber) {
        return trackRepository.getAllUserTracks(phoneNumber);
    }

    public Iterable<Track> getUserLocationsFromTimeInterval(String phoneNumber, long startDate, long stopDate) {
        return trackRepository.getUserLocationsFromTimeInterval(phoneNumber, startDate, stopDate);
    }

    public List<String> getAllUsersFromPlaceAndTimeInterval(String location, long startDate, long stopDate) {
        return trackRepository.getAllUsersFromPlaceAndTimeInterval(location, startDate, stopDate);
    }
}
