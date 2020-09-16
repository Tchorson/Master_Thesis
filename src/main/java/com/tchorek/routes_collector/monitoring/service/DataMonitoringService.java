package com.tchorek.routes_collector.monitoring.service;

import com.tchorek.routes_collector.database.json.RegistrationData;
import com.tchorek.routes_collector.database.model.DailyRecord;
import com.tchorek.routes_collector.database.model.Fugitive;
import com.tchorek.routes_collector.database.model.Registration;
import com.tchorek.routes_collector.database.repositories.AdminRepository;
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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Log4j2
@Service
public class DataMonitoringService {

    DailyTrackRepository dailyTrackRepository;
    RegistrationRepository registrationRepository;
    FugitiveRepository fugitiveRepository;
    AdminRepository adminRepository;

    Map<String, Long> lastUserActivity = new LinkedHashMap<>();
    Map<String, Long> usersWithUnknownStatus = new LinkedHashMap<>();
    Map<String, Long> fugitives = new LinkedHashMap<>();
    Map<String, Long> registrations = new LinkedHashMap<>();

    private final float MARGIN_OF_GPS_ERROR = 0.0005F;
    private final short USER_MISSING_TIME_SECONDS = 360;
    private final short TIME_FOR_USER_RETURN = 180;
    private final Float UNKNOWN_COORDINATE = null;

    @Autowired
    public DataMonitoringService(DailyTrackRepository dailyTrackRepository, RegistrationRepository registrationRepository, FugitiveRepository fugitiveRepository, AdminRepository adminRepository) {
        this.dailyTrackRepository = dailyTrackRepository;
        this.registrationRepository = registrationRepository;
        this.fugitiveRepository = fugitiveRepository;
        this.dailyTrackRepository.getAllUsersWithLastActivity()
                .forEach(dailyTracks -> lastUserActivity.put(dailyTracks.getPhoneNumber(), dailyTracks.getDate()));
        fugitives.putAll(Mapper.mapFugitivesToMap(fugitiveRepository.findAll()));
        this.registrationRepository.getAllVerifiedRegistrations().forEach(
                registration -> registrations.put(registration.getPhoneNumber(), registration.getWalkTimestamp()));
        this.adminRepository = adminRepository;
    }

    @Scheduled(cron = "0 0/5 * * * *")
    public void findAllInactiveUsers() {
        long currentTime = Timer.getCurrentTimeInSeconds();
        markUsersAsUnknown(currentTime);
        checkUsersRegistrations(currentTime);
        log.info("current unknown users: {}", usersWithUnknownStatus.keySet().toString());
        //Todo: add sms mechanism for informing inactive people
    }

    @Scheduled(cron = "30 0/3 * * * *")
    public void findAllFugitivesAmongInactiveUsers() {
        long currentTime = Timer.getCurrentTimeInSeconds();
        markUsersAsFugitive(currentTime);
        removeFugitivesUnknownStatus();
        log.info("current fugitive users {}", fugitives.keySet().toString());
    }

    @Scheduled(cron = "2 0 * * * *")
    public void clearCache() {
        lastUserActivity.clear();
        usersWithUnknownStatus.clear();
        fugitives.clear();
        registrations.clear();
    }

    @Scheduled(cron = "0 0/1 * * * *")
    public void checkUsersSessions() {
        adminRepository.removeInactiveSessions(Timer.getCurrentTimeInSeconds() - USER_MISSING_TIME_SECONDS);
    }

    private void markUsersAsFugitive(long currentTime) {
        usersWithUnknownStatus.entrySet().stream().filter(userWithUnknownStatus -> currentTime - userWithUnknownStatus.getValue() > TIME_FOR_USER_RETURN)
                .forEach(tooLongInactiveUser -> {
                    String phoneNumber = tooLongInactiveUser.getKey();
                    if (!fugitives.containsKey(phoneNumber)) {
                        addNewFugitive(phoneNumber);
                        fugitiveRepository.save(new Fugitive(phoneNumber, UNKNOWN_COORDINATE, UNKNOWN_COORDINATE, Timer.getCurrentTimeInSeconds()));
                    }
                });
    }

    public void addNewFugitive(String user) {
        if (!fugitives.containsKey(user))
            fugitives.put(user, Timer.getCurrentTimeInSeconds());
    }

    private void removeFugitivesUnknownStatus() {
        usersWithUnknownStatus.entrySet().stream().filter(unknownUser -> fugitives.containsKey(unknownUser.getKey()))
                .forEach(wantedUser -> usersWithUnknownStatus.remove(wantedUser.getKey()));
    }

    private void markUsersAsUnknown(long currentTime) {
        lastUserActivity.entrySet().stream()
                .filter(userLastTrack -> currentTime - userLastTrack.getValue() > USER_MISSING_TIME_SECONDS).forEach(userLastActivity -> {
            if (!usersWithUnknownStatus.containsKey(userLastActivity.getKey())) {
                usersWithUnknownStatus.put(userLastActivity.getKey(), Timer.getCurrentTimeInSeconds());
            }
        });
    }

    public void logRegistration(Registration registration) {
        registrations.put(registration.getPhoneNumber(), registration.getWalkTimestamp());
    }

    private void checkUsersRegistrations(long currentTime) {
        registrations.entrySet().stream().filter(
                registration -> currentTime - registration.getValue() > USER_MISSING_TIME_SECONDS &&
                        !lastUserActivity.containsKey(registration.getKey())).forEach(inactiveUserRegistration -> {
                    if (!usersWithUnknownStatus.containsKey(inactiveUserRegistration.getKey())) {
                        usersWithUnknownStatus.put(inactiveUserRegistration.getKey(), Timer.getCurrentTimeInSeconds());
                    }
                }
        );
    }

    public boolean isUserBeforeTime(String user) {
        return registrations.get(user) > Timer.getCurrentTimeInSeconds();
    }

    public boolean isUserAtHome(RegistrationData currentUserCoordinates) throws Exception {
        Optional<Registration> userCoordinatesDuringRegistration = registrationRepository.findById(currentUserCoordinates.getUserData());
        if (userCoordinatesDuringRegistration.isEmpty() || !userCoordinatesDuringRegistration.get().getApproved())
            throw new Exception("Unapproved user");
        if (isUserAtHome(userCoordinatesDuringRegistration.get(), currentUserCoordinates)) {
            try {
                removeUserFromMonitoring(currentUserCoordinates.getUserData());
                return true;
            } catch (NotFoundException e) {
                throw new NotFoundException("User not in database");
            }
        }
        return false;
    }

    private boolean isUserAtHome(Registration userEntryPosition, RegistrationData currentUserData) {
        return Math.abs(currentUserData.getLatitude() - userEntryPosition.getLatitude()) <= MARGIN_OF_GPS_ERROR
                && Math.abs(currentUserData.getLongitude() - userEntryPosition.getLongitude()) <= MARGIN_OF_GPS_ERROR;
    }

    public boolean checkIfUserIsRegistered(String userNumber) {
        return registrationRepository.findUserRegistration(userNumber) > 0;
    }

    public Set<String> getAllMissingUsers() {
        return usersWithUnknownStatus.keySet();
    }

    public void saveUserActivity(DailyRecord userDailyRecord) {
        lastUserActivity.put(userDailyRecord.getPhoneNumber(), userDailyRecord.getDate());
        usersWithUnknownStatus.remove(userDailyRecord.getPhoneNumber());
    }

    public void removeUserFromMonitoring(String userNumber) throws NotFoundException {
        if (lastUserActivity.containsKey(userNumber) || usersWithUnknownStatus.containsKey(userNumber)) {
            lastUserActivity.remove(userNumber);
            usersWithUnknownStatus.remove(userNumber);
        } else
            throw new NotFoundException("Removing from collections failed");
    }

    public void removeFugitiveFromService(String user) throws Exception {
        if (fugitives.containsKey(user)) {
            fugitives.remove(user);
            fugitiveRepository.deleteById(user);
        } else
            throw new Exception("NOT FOUND");
    }
}
