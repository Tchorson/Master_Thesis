package com.tchorek.routes_collector.database.service;

import com.tchorek.routes_collector.database.model.Track;
import com.tchorek.routes_collector.database.repositories.TrackRepository;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Log4j2
@NoArgsConstructor
@Service
public class DatabaseService {

    TrackRepository trackRepository;

    @Autowired
    public DatabaseService(TrackRepository trackRepository) {
        this.trackRepository = trackRepository;
    }

    public void saveTrackOfUser(Track userTrack) {
        trackRepository.save(userTrack);
    }

    public void unsubscribeUser(String phoneNumber){
        if (trackRepository.findById(phoneNumber).isPresent()) {
            removeUserRoute(phoneNumber);
        }
    }

    public void removeUserRoute(String userNumber) {
        trackRepository.deleteUserRoute(userNumber);
    }

    public void clearDatabase() {
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
