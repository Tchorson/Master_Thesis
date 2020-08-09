package com.tchorek.routes_collector.monitoring.service;

import com.tchorek.routes_collector.database.json.RegistrationData;
import com.tchorek.routes_collector.database.model.DailyRecord;
import com.tchorek.routes_collector.database.model.Fugitive;
import com.tchorek.routes_collector.database.model.Registration;
import com.tchorek.routes_collector.database.repositories.DailyTrackRepository;
import com.tchorek.routes_collector.database.repositories.FugitiveRepository;
import com.tchorek.routes_collector.database.repositories.RegistrationRepository;
import com.tchorek.routes_collector.utils.Mapper;
import com.tchorek.routes_collector.utils.Timer;
import javassist.NotFoundException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.management.openmbean.KeyAlreadyExistsException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

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
    private final Float UNKNOWN_COORDINATE  = null;

    @Autowired
    public MonitoringService(DailyTrackRepository dailyTrackRepository, RegistrationRepository registrationRepository, FugitiveRepository fugitiveRepository) {
        this.dailyTrackRepository = dailyTrackRepository;
        this.registrationRepository = registrationRepository;
        this.fugitiveRepository = fugitiveRepository;
        this.dailyTrackRepository.getAllUsersWithLastActivity()
                .forEach(dailyTracks -> lastUserActivity.put(dailyTracks.getPhoneNumber(), dailyTracks.getDate()));
        fugitives.putAll(Mapper.mapFugitivesToMap(fugitiveRepository.findAll()));
    }

    @Scheduled(cron = "0 0/5 * * * *")
    public void findAllInactiveUsers() {
        long currentTime = Timer.getCurrentTimeInSeconds();
        markUsersAsUnknown(currentTime);
        log.info("current unknown users: \n{}", usersWithUnknownStatus.keySet().toString());
        //Todo: add sms mechanism for inactive people
    }

    @Scheduled(cron = "30 0/3 * * * *")
    public void findAllFugitives(){
        long currentTime = Timer.getCurrentTimeInSeconds();
        removeReactivatedUsers(currentTime);
        markUsersAsFugitive(currentTime);
        removeFugitivesFromUnknownMap();
        log.info("current fugitive users {}", fugitives.keySet().toString());
    }

    private void markUsersAsFugitive(long currentTime){
        usersWithUnknownStatus.entrySet().stream().filter(stringLongEntry -> currentTime - stringLongEntry.getValue() > MISSING_PERIOID)
                .forEach(userWithUnknownStatus -> {
                    String phoneNumber = userWithUnknownStatus.getKey();
                    if (!fugitives.containsKey(phoneNumber)) {
                        fugitives.put(phoneNumber, Timer.getCurrentTimeInSeconds());
                        fugitiveRepository.save(new Fugitive(phoneNumber, UNKNOWN_COORDINATE, UNKNOWN_COORDINATE, Timer.getCurrentTimeInSeconds()));
                    }
                });
    }

    public void addNewFugitive(RegistrationData verificationData){
        if(fugitives.containsKey(verificationData.getUserData()))
            throw new KeyAlreadyExistsException("Fugitive is already in system");
        fugitives.put(verificationData.getUserData(), Timer.getCurrentTimeInSeconds());
    }

    private void removeFugitivesFromUnknownMap(){
        usersWithUnknownStatus.entrySet().stream().filter(unknownUser -> fugitives.containsKey(unknownUser.getKey()))
                .forEach(wantedUser -> usersWithUnknownStatus.remove(wantedUser.getKey()));
    }

    private void removeReactivatedUsers(long currentTime){
        usersWithUnknownStatus.entrySet().stream().filter(unknownUser -> currentTime - lastUserActivity.get(unknownUser.getKey()) < INACTIVITY_PERIOID)
                .forEach(reactivatedUser -> usersWithUnknownStatus.remove(reactivatedUser.getKey()));
    }

    private void markUsersAsUnknown(long currentTime){
        lastUserActivity.entrySet().stream()
                .filter(userLastTrack -> currentTime - userLastTrack.getValue() > INACTIVITY_PERIOID).forEach(userLastActivity -> {
            if (!usersWithUnknownStatus.containsKey(userLastActivity.getKey())) {
                usersWithUnknownStatus.put(userLastActivity.getKey(), Timer.getCurrentTimeInSeconds());
            }
        });
    }

    public boolean isUserAtHome(RegistrationData currentUserCoordinates) throws Exception {
        Optional<Registration> userCoordinatesDuringRegistration = registrationRepository.findById(currentUserCoordinates.getUserData());
        if (userCoordinatesDuringRegistration.isEmpty() || !userCoordinatesDuringRegistration.get().getApproved()) throw new Exception("Unapproved user");
        if (isUserAtHome(userCoordinatesDuringRegistration.get(), currentUserCoordinates)) {
            try {
                removeUserFromMonitoring(currentUserCoordinates.getUserData());
                return true;
            }catch (NotFoundException e){
                throw new NotFoundException("User not in database");
            }
        }
        return false;
    }

    private boolean isUserAtHome(Registration userEntryPosition, RegistrationData currentUserData) {
        return Math.abs(currentUserData.getLatitude() - userEntryPosition.getLatitude()) <= MARGIN_OF_ERROR
                && Math.abs(currentUserData.getLongitude() - userEntryPosition.getLongitude()) <= MARGIN_OF_ERROR;
    }

    @Scheduled(cron = "5 0 * * * *")
    public void clearCache() {
        lastUserActivity.clear();
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

    public void saveUserActivity(DailyRecord userDailyRecord) {
        lastUserActivity.put(userDailyRecord.getPhoneNumber(), userDailyRecord.getDate());
    }

    public void removeUserFromMonitoring(String userNumber) throws NotFoundException {
        if(lastUserActivity.containsKey(userNumber) || usersWithUnknownStatus.containsKey(userNumber)){
            lastUserActivity.remove(userNumber);
            usersWithUnknownStatus.remove(userNumber);
        }
        else
            throw new NotFoundException("Removing from collections failed");
    }

    public void removeFugitiveFromService(String user) throws Exception {
        if(fugitives.containsKey(user)){
            fugitives.remove(user);
            fugitiveRepository.deleteById(user);
        }
        else
            throw new Exception("NOT FOUND");
    }
}
