package com.tchorek.routes_collector.monitoring.service;

import com.tchorek.routes_collector.database.json.RegistrationData;
import com.tchorek.routes_collector.database.model.DailyTracks;
import com.tchorek.routes_collector.database.model.Fugitive;
import com.tchorek.routes_collector.database.model.Registration;
import com.tchorek.routes_collector.database.repositories.DailyTrackRepository;
import com.tchorek.routes_collector.database.repositories.FugitiveRepository;
import com.tchorek.routes_collector.database.repositories.RegistrationRepository;
import com.tchorek.routes_collector.utils.Mapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Log4j2
@Service
public class MonitoringService {

    DailyTrackRepository dailyTrackRepository;
    RegistrationRepository registrationRepository;
    FugitiveRepository fugitiveRepository;

    Map<String, Long> lastUserActivity = new LinkedHashMap<>();
    Map<String, Long> usersWithUnknownStatus = new LinkedHashMap<>();
    Map<String, Long> fugitives = new LinkedHashMap<>();

    private final float MARGIN_OF_ERROR = 0.0005F;
    private final short INACTIVITY_PERIOID = 360;
    private final short MISSING_PERIOID = 180;

    @Scheduled(cron = "0 0/5 * * * *")
    public void findAllInactiveUsers() {
        long currentTime = Instant.now().getEpochSecond();
        lastUserActivity.entrySet().stream()
                .filter(userLastTrack -> currentTime - userLastTrack.getValue() > INACTIVITY_PERIOID).forEach(userLastActivity -> {
            if (!usersWithUnknownStatus.containsKey(userLastActivity.getKey())) {
                usersWithUnknownStatus.put(userLastActivity.getKey(), Instant.now().getEpochSecond());
            }
        });
        log.info("current unknown users: \n{}", usersWithUnknownStatus.keySet().toString());
        //Todo: add sms mechanism for inactive people
    }

    @Scheduled(cron = "30 0/3 * * * *")
    public void findAllFugitives(){

        long currentTime = Instant.now().getEpochSecond();
        usersWithUnknownStatus.entrySet().stream().filter(unknownUser -> currentTime - lastUserActivity.get(unknownUser.getKey()) < INACTIVITY_PERIOID)
                .forEach(reactivatedUser -> usersWithUnknownStatus.remove(reactivatedUser.getKey()));

        usersWithUnknownStatus.entrySet().stream().filter(stringLongEntry -> currentTime - stringLongEntry.getValue() > MISSING_PERIOID)
                .forEach(userUnknownStatus -> {
                    System.out.println(userUnknownStatus.getKey());
                    if (!fugitives.containsKey(userUnknownStatus.getKey())) {
                        fugitives.put(userUnknownStatus.getKey(), Instant.now().getEpochSecond());
                        fugitiveRepository.save(new Fugitive(userUnknownStatus.getKey(), null, null, userUnknownStatus.getValue()));
                    }
                });

        usersWithUnknownStatus.entrySet().stream().filter(unknownUser -> currentTime - usersWithUnknownStatus.get(unknownUser.getKey()) > MISSING_PERIOID && fugitives.containsKey(unknownUser.getKey()))
                .forEach(wantedUser -> usersWithUnknownStatus.remove(wantedUser.getKey()));
        log.info("current fugitive users {}", fugitives.keySet().toString());
    }

    public void claimUserAsFugitive(RegistrationData currentUserData) {
        fugitiveRepository.save(Mapper.mapJsonToFugitive(currentUserData));
    }

    public boolean isUserCurrentLocationValid(RegistrationData currentUserCoordinates) throws Exception {
        Optional<Registration> userEntryCoordinates = registrationRepository.findById(currentUserCoordinates.getUserData());
        if (userEntryCoordinates.isEmpty() || !userEntryCoordinates.get().getApproved()) throw new Exception("Unapproved user or a fugitive");
        if (isUserAtHome(userEntryCoordinates.get(), currentUserCoordinates)) {
            removeUserFromMonitoring(currentUserCoordinates.getUserData());
            registrationRepository.deleteById(currentUserCoordinates.getUserData());
            return true;
        }
        return false;
    }

    private boolean isUserAtHome(Registration userEntryPosition, RegistrationData currentUserData) {
        return Math.abs(currentUserData.getLatitude() - userEntryPosition.getLatitude()) <= MARGIN_OF_ERROR
                && Math.abs(currentUserData.getLongitude() - userEntryPosition.getLongitude()) <= MARGIN_OF_ERROR;
    }

    @Scheduled(cron = "4 0 * * * *")
    public void clearCache() {
        lastUserActivity.clear();
    }

    @Autowired
    public MonitoringService(DailyTrackRepository dailyTrackRepository, RegistrationRepository registrationRepository, FugitiveRepository fugitiveRepository) {
        this.dailyTrackRepository = dailyTrackRepository;
        this.registrationRepository = registrationRepository;
        this.fugitiveRepository = fugitiveRepository;
        this.dailyTrackRepository.getAllUsersWithLastActivity()
                .forEach(dailyTracks -> lastUserActivity.put(dailyTracks.getPhoneNumber(), dailyTracks.getDate()));
        fugitives.putAll(Mapper.mapFugitivesToMap(fugitiveRepository.findAll()));
    }

    public void approveUser(Registration approvedUser) {
        registrationRepository.save(approvedUser);
    }

    public boolean checkIfUserIsRegisteredAndEligibleForWalk(String userNumber) {
        return registrationRepository.selectApprovedUser(userNumber) > 0;
    }

    public Set<String> getAllMissingUsers() {
        return usersWithUnknownStatus.keySet();
    }

    public void saveUserActivity(DailyTracks userDailyTracks) {
        lastUserActivity.put(userDailyTracks.getPhoneNumber(), userDailyTracks.getDate());
    }

    public void removeUserFromMonitoring(String userNumber) {
        lastUserActivity.remove(userNumber);
        usersWithUnknownStatus.remove(userNumber);
    }
}
