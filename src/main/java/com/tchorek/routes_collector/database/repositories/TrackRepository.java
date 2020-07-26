package com.tchorek.routes_collector.database.repositories;

import com.tchorek.routes_collector.database.model.Track;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface TrackRepository extends CrudRepository<Track,String> {

    @Query(value = "SELECT user_id FROM user_routes WHERE device_id = :location AND timestamp >= :time", nativeQuery = true)
     List<String> getListOfUsersByLocationAndTime(@Param("location") String location, @Param("time") long timestamp);

    @Query(value = "SELECT DISTINCT user_id FROM user_routes WHERE timestamp BETWEEN :startTimestamp AND :stopTimestamp AND device_id IN " +
            "(SELECT device_id FROM user_routes WHERE user_id = :userNumber)",nativeQuery = true)
     List<String> getListOfUsersWhoMetUserRecently(@Param("userNumber") String phone_number, @Param("startTimestamp") long startTimestamp, @Param("stopTimestamp") long stopTimestamp);

    @Query(value= "DELETE FROM user_routes WHERE user_id = :userNumber" ,nativeQuery = true)
     void deleteUserHistory(@Param("userNumber") String number);

    @Query(value = "SELECT user_id, timestamp, device_id FROM user_routes WHERE user_id = :userNumber", nativeQuery = true)
     List<Track> getAllUserTracks(@Param("userNumber")String phoneNumber);

    @Query(value= "SELECT user_id, timestamp, device_id FROM user_routes WHERE user_id = :userNumber AND timestamp  BETWEEN :start AND :end", nativeQuery = true)
    List<Track> getUserLocationsFromTimeInterval(@Param("userNumber")String phoneNumber, @Param("start")long startDate, @Param("end")long endDate);

    @Query(value= "SELECT user_id FROM user_routes WHERE device_id = :location AND timestamp  BETWEEN :start AND :end", nativeQuery = true)
    List<String> getAllUsersFromPlaceAndTimeInterval(@Param("location")String location, @Param("start")long startDate, @Param("end")long endDate);

    @Query(value= "SELECT DISTINCT user_id, timestamp, device_id FROM user_routes ORDER BY timestamp DESC", nativeQuery = true)
    List<Track> getAllUsersWithLastActivityTime();
}
