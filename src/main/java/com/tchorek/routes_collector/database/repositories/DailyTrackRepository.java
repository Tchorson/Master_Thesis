package com.tchorek.routes_collector.database.repositories;

import com.tchorek.routes_collector.database.model.DailyRecord;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DailyTrackRepository extends CrudRepository<DailyRecord,String> {

    @Query(value = "SELECT user_id FROM user_routes_daily WHERE device_id = :location AND timestamp >= :time", nativeQuery = true)
     List<DailyRecord> getListOfUsersByLocationAndTime(@Param("location") String location, @Param("time") long timestamp);

    @Query(value = "SELECT DISTINCT user_id FROM user_routes_daily WHERE timestamp BETWEEN :startTimestamp AND :stopTimestamp AND device_id IN " +
            "(SELECT device_id FROM user_routes_daily WHERE user_id = :userNumber)", nativeQuery = true)
     List<String> getUsersWhoMetUserRecently(@Param("userNumber") String phone_number, @Param("startTimestamp") long startTimestamp, @Param("stopTimestamp") long stopTimestamp);

    @Query(value = "SELECT user_id, timestamp, device_id FROM user_routes_daily WHERE user_id = :userNumber", nativeQuery = true)
     List<DailyRecord> getUserDailyRoute(@Param("userNumber")String phoneNumber);

    @Query(value= "SELECT user_id FROM user_routes_daily WHERE device_id = :location AND timestamp  BETWEEN :start AND :end", nativeQuery = true)
    List<String> getAllUsersFromParticularPlaceAndTime(@Param("location")String location, @Param("start")long startDate, @Param("end")long endDate);

    @Query(value= "SELECT DISTINCT user_id, timestamp, device_id FROM user_routes_daily ORDER BY timestamp DESC", nativeQuery = true)
    List<DailyRecord> getAllUsersWithLastActivity();
}
