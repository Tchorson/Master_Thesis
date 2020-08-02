package com.tchorek.routes_collector.monitoring.service;

import com.tchorek.routes_collector.database.json.RegistrationData;
import com.tchorek.routes_collector.database.model.DailyTracks;
import com.tchorek.routes_collector.database.model.Registration;
import com.tchorek.routes_collector.database.repositories.DailyTrackRepository;
import com.tchorek.routes_collector.database.repositories.RegistrationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MonitoringService {

    DailyTrackRepository dailyTrackRepository;
    RegistrationRepository registrationRepository;

    Map<String, Long> lastUserActivity = new LinkedHashMap<>();
    Set<String> usersWithUnknownStatus = new LinkedHashSet<>();

    private static final float MARGIN_OF_ERROR = 0.0005F;

    @Scheduled(cron = "0 0/5 * * * *")
    public void findAllInactiveUsers() {
        long currentTime = Instant.now().getEpochSecond();
        Set<String> newUnknownUsers = lastUserActivity.entrySet().stream()
                .filter(userLastTrack -> currentTime - userLastTrack.getValue() > 360)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        //Todo: add sms mechanism for inactive people
        usersWithUnknownStatus.clear();
        usersWithUnknownStatus.addAll(newUnknownUsers);
    }

    public boolean isUserCurrentLocationValid(RegistrationData currentUserCoordinates) throws Exception {
       Optional<Registration> userEntryCoordinates = registrationRepository.findById(currentUserCoordinates.getUserData());
       if(userEntryCoordinates.isEmpty()) throw new Exception("Unapproved user");
       if(isUserAtHome(userEntryCoordinates.get(), currentUserCoordinates)){
           removeUserFromMonitoring(currentUserCoordinates.getUserData());
           registrationRepository.deleteById(currentUserCoordinates.getUserData());
           return true;
       }
       return false;
    }

    private boolean isUserAtHome(Registration userEntryPosition, RegistrationData currentUserData){
        return Math.abs(currentUserData.getLatitude() - userEntryPosition.getLatitude()) <= MARGIN_OF_ERROR
                && Math.abs(currentUserData.getLongitude() - userEntryPosition.getLongitude()) <= MARGIN_OF_ERROR;
    }

    @Scheduled(cron = "4 0 * * * *")
    public void clearCache() {
        lastUserActivity.clear();
    }

    @Autowired
    public MonitoringService(DailyTrackRepository dailyTrackRepository, RegistrationRepository registrationRepository) {
        this.dailyTrackRepository = dailyTrackRepository;
        this.registrationRepository = registrationRepository;
        dailyTrackRepository.getAllUsersWithLastActivity()
                .forEach(dailyTracks -> lastUserActivity.put(dailyTracks.getPhoneNumber(), dailyTracks.getDate()));
    }

    public void approveUser(Registration approvedUser){
        registrationRepository.save(approvedUser);
    }

    public boolean checkIfUserIsRegisteredAndEligibleForWalk(String userNumber){
        return registrationRepository.selectApprovedUser(userNumber) > 0;
    }

    public Set<String> getAllMissingUsers() {
        return usersWithUnknownStatus;
    }

    public void saveUserActivity(DailyTracks userDailyTracks) {
        lastUserActivity.put(userDailyTracks.getPhoneNumber(), userDailyTracks.getDate());
    }

    public void removeUserFromMonitoring(String userNumber) {
        lastUserActivity.remove(userNumber);
        usersWithUnknownStatus.remove(userNumber);
    }
}
