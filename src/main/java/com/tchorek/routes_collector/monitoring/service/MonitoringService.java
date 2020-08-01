package com.tchorek.routes_collector.monitoring.service;

import com.tchorek.routes_collector.database.model.Track;
import com.tchorek.routes_collector.database.repositories.TrackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MonitoringService {

    TrackRepository trackRepository;

    Map<String, Long> lastUserActivity = new LinkedHashMap<>();
    Set<String> usersWithUnknownStatus = new LinkedHashSet<>();

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

    @Autowired
    public MonitoringService(TrackRepository trackRepository) {
        this.trackRepository = trackRepository;
        trackRepository.getAllUsersWithLastActivity()
                .forEach(track -> lastUserActivity.put(track.getPhoneNumber(), track.getDate()));
    }

    public Set<String> getAllMissingUsers() {
        return usersWithUnknownStatus;
    }

    public void saveUserActivity(Track userTrack) {
        lastUserActivity.put(userTrack.getPhoneNumber(), userTrack.getDate());
    }

    public void removeUserFromActivityList(String userNumber) {
        lastUserActivity.remove(userNumber);
    }

    public void clearCache(){
        lastUserActivity.clear();
        usersWithUnknownStatus.clear();
    }
}