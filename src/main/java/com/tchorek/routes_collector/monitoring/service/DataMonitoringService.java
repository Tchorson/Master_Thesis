package com.tchorek.routes_collector.monitoring.service;

import com.tchorek.routes_collector.database.json.RegistrationData;
import com.tchorek.routes_collector.database.model.*;
import com.tchorek.routes_collector.database.repositories.*;
import com.tchorek.routes_collector.utils.Mapper;
import com.tchorek.routes_collector.utils.Timer;
import javassist.NotFoundException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;

@Log4j2
@Service
public class DataMonitoringService {

    DailyTrackRepository dailyTrackRepository;
    HangoutRegistrationsRepository hangoutRegistrationsRepository;
    FugitiveRepository fugitiveRepository;
    AdminRepository adminRepository;
    SickPeopleRepository sickPeopleRepository;
    UserRegistrationRepository userRegistrationRepository;

    Map<String, Long> lastUserActivity = new LinkedHashMap<>();
    Map<String, Long> usersWithUnknownStatus = new LinkedHashMap<>();
    Map<String, Long> fugitives = new LinkedHashMap<>();
    Map<String, Long> hangoutsTime = new LinkedHashMap<>();
    Map<String, Long> returnTime = new LinkedHashMap<>();
    List<SickPerson> sickPersonList = new LinkedList<>();
    List<RegisteredUser> registeredUsers = new LinkedList<>();
    private final float MARGIN_OF_GPS_ERROR = 0.0005F;
    private final long DELTA_TIME = 604800;
    private final short USER_MISSING_TIME_SECONDS = 360;
    private final short TIME_FOR_USER_RETURN = 180;
    private final Float UNKNOWN_COORDINATE = null;
    private final boolean UNREPORTED = false;
    private Integer sickPeopleCounter = null;

    @Autowired
    public DataMonitoringService(DailyTrackRepository dailyTrackRepository, HangoutRegistrationsRepository hangoutRegistrationsRepository, FugitiveRepository fugitiveRepository, AdminRepository adminRepository, SickPeopleRepository sickPeopleRepository, UserRegistrationRepository userRegistrationRepository) {
        this.dailyTrackRepository = dailyTrackRepository;
        this.hangoutRegistrationsRepository = hangoutRegistrationsRepository;
        this.fugitiveRepository = fugitiveRepository;
        this.dailyTrackRepository.getAllUsersWithLastActivity()
                .forEach(dailyTracks -> lastUserActivity.put(dailyTracks.getPhoneNumber(), dailyTracks.getDate()));
        fugitives.putAll(Mapper.mapFugitivesToMap(fugitiveRepository.findAll()));
        this.hangoutRegistrationsRepository.getAllVerifiedRegistrations().forEach(
                registration -> {
                    hangoutsTime.put(registration.getPhoneNumber(), registration.getWalkTimestamp());
                    returnTime.put(registration.getPhoneNumber(), registration.getReturnDate());
                });
        this.adminRepository = adminRepository;
        this.sickPeopleRepository = sickPeopleRepository;
        this.userRegistrationRepository = userRegistrationRepository;
        monitorSickUsers();
        fetchRegisteredUsers();
    }

    @Scheduled(cron = "0 0/5 * * * *")
    public void findAllInactiveUsers() {
        long currentTime = Timer.getCurrentTimeInSeconds();
        markUsersAsUnknown(currentTime);
        checkUsersActivityTime(currentTime);
        log.info("current unknown users: {}", usersWithUnknownStatus.keySet().toString());
        //Todo: add sms mechanism for informing inactive people
    }

    @Scheduled(cron = "0 0/10 * * * *")
    public void monitorSickUsers() {
        sickPeopleRepository.removeReportedPeople(Timer.getCurrentTimeInSeconds()-DELTA_TIME);

        if (sickPeopleCounter == null) {
            sickPeopleCounter = sickPeopleRepository.countPeople();
            sickPersonList = sickPeopleRepository.collectUnreportedPeople();
        } else {
            if (sickPeopleRepository.countPeople() != sickPeopleCounter) {
                log.info("updating list of sick people");
                sickPersonList = sickPeopleRepository.collectUnreportedPeople();
            }
        }
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
        hangoutsTime.clear();
        returnTime.clear();
    }

    @Scheduled(cron = "0 0/4 * * * *")
    public void checkUsersSessions() {
        adminRepository.removeInactiveSessions(Timer.getCurrentTimeInSeconds() - USER_MISSING_TIME_SECONDS);
    }

    public List<SickPerson> getSickPeopleList() {
        return sickPersonList;
    }

    public void reportSickPeople(List<String> ids) {
        sickPeopleRepository.markUnreportedAsReported(ids, Timer.getCurrentTimeInSeconds());
        sickPersonList = sickPeopleRepository.collectUnreportedPeople();
    }

    public void addSickPerson(SickPerson newPerson) {
        sickPeopleRepository.save(newPerson);
        sickPersonList.add(newPerson);
    }

    private void fetchRegisteredUsers(){
        userRegistrationRepository.findAll().forEach(user -> registeredUsers.add(user));
    }

    public boolean isUserReported(String id){
        return sickPersonList.stream().anyMatch(sickPerson -> sickPerson.getPhoneNumber().equals(id));
    }

    public void registerUser(RegisteredUser user){
        userRegistrationRepository.save(user);
        registeredUsers.add(user);
    }

    public void deleteUser(RegisteredUser user){
        if(!isUserRegistered(user))
            return;
        userRegistrationRepository.removeUserFromDatabase(user.getPhoneNumber(), user.getEmail(), user.getToken());
        registeredUsers.remove(user);
    }

    private boolean isUserRegistered(RegisteredUser user){
        return registeredUsers.contains(user);
    }

    private void markUsersAsFugitive(long currentTime) {
        usersWithUnknownStatus.entrySet().stream().filter(userWithUnknownStatus -> currentTime - userWithUnknownStatus.getValue() > TIME_FOR_USER_RETURN)
            .forEach(tooLongInactiveUser -> {
                String phoneNumber = tooLongInactiveUser.getKey();
                if (!fugitives.containsKey(phoneNumber)) {
                    addNewFugitive(phoneNumber);
                    fugitiveRepository.save(new Fugitive(phoneNumber, UNKNOWN_COORDINATE, UNKNOWN_COORDINATE, Timer.getCurrentTimeInSeconds(), UNREPORTED));
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
            if (!usersWithUnknownStatus.containsKey(userLastActivity.getKey()))
                usersWithUnknownStatus.put(userLastActivity.getKey(), Timer.getCurrentTimeInSeconds());
        });
    }

    public void logUserActivityTime(Registration registration) {
        hangoutsTime.put(registration.getPhoneNumber(), registration.getWalkTimestamp());
        returnTime.put(registration.getPhoneNumber(), registration.getReturnDate());
    }

    private void checkUsersActivityTime(long currentTime) {
        hangoutsTime.entrySet().stream().filter(
            registration -> currentTime - registration.getValue() > USER_MISSING_TIME_SECONDS &&
                    !lastUserActivity.containsKey(registration.getKey())).forEach(inactiveUserRegistration -> {
                if (!usersWithUnknownStatus.containsKey(inactiveUserRegistration.getKey()))
                    usersWithUnknownStatus.put(inactiveUserRegistration.getKey(), Timer.getCurrentTimeInSeconds());
            }
        );
        returnTime.entrySet().stream().filter(
            registration -> currentTime > registration.getValue() &&
                    !lastUserActivity.containsKey(registration.getKey())).forEach(userWithExceededTime -> {
                if (!usersWithUnknownStatus.containsKey(userWithExceededTime.getKey()))
                    usersWithUnknownStatus.put(userWithExceededTime.getKey(), Timer.getCurrentTimeInSeconds());
            }
        );
    }

    public boolean isUserBeforeTime(String user) {
        return hangoutsTime.get(user) > Timer.getCurrentTimeInSeconds();
    }

    public Map<Long, Boolean> isUserAfterTime(String user) {
        Long userReturnTime = returnTime.get(user);
        if (userReturnTime > Timer.getCurrentTimeInSeconds())
            return Collections.singletonMap(userReturnTime, false);
        return Collections.singletonMap(userReturnTime, true);

    }

    public boolean isUserAtHome(RegistrationData currentUserCoordinates) throws Exception {
        Optional<Registration> userCoordinatesDuringRegistration = hangoutRegistrationsRepository.findById(currentUserCoordinates.getUserData());
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
        return hangoutRegistrationsRepository.findUserRegistration(userNumber) > 0;
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
