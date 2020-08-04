package com.tchorek.routes_collector.database.repositories;

import com.tchorek.routes_collector.database.model.DailyTracks;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface DailyTrackRepository extends CrudRepository<DailyTracks,String> {

    @Query(value = "SELECT user_id FROM user_routes_daily WHERE device_id = :location AND timestamp >= :time", nativeQuery = true)
     List<DailyTracks> getListOfUsersByLocationAndTime(@Param("location") String location, @Param("time") long timestamp);

    @Query(value = "SELECT DISTINCT user_id FROM user_routes_daily WHERE timestamp BETWEEN :startTimestamp AND :stopTimestamp AND device_id IN " +
            "(SELECT device_id FROM user_routes_daily WHERE user_id = :userNumber)",nativeQuery = true)
     List<String> getUsersWhoMetUserRecently(@Param("userNumber") String phone_number, @Param("startTimestamp") long startTimestamp, @Param("stopTimestamp") long stopTimestamp);

    @Modifying
    @Transactional
    @Query(value= "DELETE FROM user_routes_daily WHERE user_id = :userNumber" ,nativeQuery = true)
     void deleteUserRoute(@Param("userNumber") String number);

    @Query(value = "SELECT user_id, timestamp, device_id FROM user_routes_daily WHERE user_id = :userNumber", nativeQuery = true)
     List<DailyTracks> getUserRoute(@Param("userNumber")String phoneNumber);

    @Query(value= "SELECT user_id, timestamp, device_id FROM user_routes_daily WHERE user_id = :userNumber AND timestamp  BETWEEN :start AND :end", nativeQuery = true)
    List<DailyTracks> getUserRouteFromParticularTime(@Param("userNumber")String phoneNumber, @Param("start")long startDate, @Param("end")long endDate);

    @Query(value= "SELECT user_id FROM user_routes_daily WHERE device_id = :location AND timestamp  BETWEEN :start AND :end", nativeQuery = true)
    List<String> getAllUsersFromParticularPlaceAndTime(@Param("location")String location, @Param("start")long startDate, @Param("end")long endDate);

    @Query(value= "SELECT DISTINCT user_id, timestamp, device_id FROM user_routes_daily ORDER BY timestamp DESC", nativeQuery = true)
    List<DailyTracks> getAllUsersWithLastActivity();
}
