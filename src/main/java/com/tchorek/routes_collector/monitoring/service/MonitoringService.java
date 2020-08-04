package com.tchorek.routes_collector.monitoring.service;

import com.tchorek.routes_collector.database.json.RegistrationData;
import com.tchorek.routes_collector.database.model.DailyTracks;
import com.tchorek.routes_collector.database.model.Fugitive;
import com.tchorek.routes_collector.database.model.Registration;
import com.tchorek.routes_collector.database.repositories.DailyTrackRepository;
import com.tchorek.routes_collector.database.repositories.FugitiveRepository;
import com.tchorek.routes_collector.database.repositories.RegistrationRepository;
import com.tchorek.routes_collector.utils.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

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
                .filter(userLastTrack -> currentTime - userLastTrack.getValue() > INACTIVITY_PERIOID).peek(userLastActivity -> {
                    if(!usersWithUnknownStatus.containsKey(userLastActivity.getKey()))
                    usersWithUnknownStatus.put(userLastActivity.getKey(),Instant.now().getEpochSecond());
                });
        //Todo: add sms mechanism for inactive people
    }

    @Scheduled(cron = "30 0/5 * * * *")
    public void findAllFugitives(){

        usersWithUnknownStatus.entrySet().stream().filter(unknownUser -> lastUserActivity.get(unknownUser.getKey()) < INACTIVITY_PERIOID)
                .forEach(foundUser -> {
                    fugitives.remove(foundUser.getKey());
                    usersWithUnknownStatus.remove(foundUser.getKey());
                });

        long currentTime = Instant.now().getEpochSecond();
        usersWithUnknownStatus.entrySet().stream().filter(stringLongEntry -> currentTime - stringLongEntry.getValue() > MISSING_PERIOID)
                .peek(userUnknownStatus -> {
            if (!fugitives.containsKey(userUnknownStatus.getKey())){
                fugitives.put(userUnknownStatus.getKey(), Instant.now().getEpochSecond());
                fugitiveRepository.save(new Fugitive(userUnknownStatus.getKey(), null, null, userUnknownStatus.getValue()));
            }
        });
    }

    public void claimUserAsFugitive(RegistrationData currentUserData){
        fugitiveRepository.save(Mapper.mapJsonToFugitive(currentUserData));
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
    public MonitoringService(DailyTrackRepository dailyTrackRepository, RegistrationRepository registrationRepository, FugitiveRepository fugitiveRepository) {
        this.dailyTrackRepository = dailyTrackRepository;
        this.registrationRepository = registrationRepository;
        this.fugitiveRepository = fugitiveRepository;
        dailyTrackRepository.getAllUsersWithLastActivity()
                .forEach(dailyTracks -> lastUserActivity.put(dailyTracks.getPhoneNumber(), dailyTracks.getDate()));
        fugitives.putAll(Mapper.mapFugitivesToMap(fugitiveRepository.findAll()));
    }

    public void approveUser(Registration approvedUser){
        registrationRepository.save(approvedUser);
    }

    public boolean checkIfUserIsRegisteredAndEligibleForWalk(String userNumber){
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
