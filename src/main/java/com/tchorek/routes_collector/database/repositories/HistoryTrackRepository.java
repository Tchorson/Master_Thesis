package com.tchorek.routes_collector.database.repositories;

import com.tchorek.routes_collector.database.model.HistoryTracks;
import com.tchorek.routes_collector.database.model.LightHistoryTrack;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;

@Repository
public interface HistoryTrackRepository extends CrudRepository<HistoryTracks, String> {

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO user_routes_history (user_id, device_id, timestamp) " +
            "SELECT user_id, device_id, timestamp FROM user_routes_daily;", nativeQuery = true)
    void transferDailyDataToHistory();

    @Query(value = "SELECT DISTINCT user_id FROM user_routes_history WHERE timestamp BETWEEN :startTimestamp AND :stopTimestamp AND device_id IN " +
            "(SELECT device_id FROM user_routes_history WHERE user_id = :userNumber) AND user_id <> :userNumber", nativeQuery = true)
    List<String> getUsersWhoMetUser(@Param("userNumber") String phone_number, @Param("startTimestamp") long startTimestamp, @Param("stopTimestamp") long stopTimestamp);

    @Query(value = "SELECT user_id as name, device_id as device, min(timestamp) as date FROM user_routes_history " +
            "WHERE timestamp BETWEEN :startTimestamp AND :stopTimestamp AND device_id IN " +
            "(SELECT device_id FROM user_routes_history WHERE user_id = :userNumber) AND user_id <> :userNumber " +
            " group by user_id, device_id", nativeQuery = true)
    Set<LightHistoryTrack> getUsers(@Param("userNumber") String phone_number, @Param("startTimestamp") long startTimestamp, @Param("stopTimestamp") long stopTimestamp);

    @Query(value = "SELECT user_id, device_id, max(TIMESTAMP) as timestamp FROM user_routes_history WHERE user_id = :previousUser AND " +
            " TIMESTAMP < :particularTime AND device_id = :particularPlace GROUP BY user_id, device_id;", nativeQuery = true)
    HistoryTracks getUserWhoMetUserRecently(@Param("previousUser") String previousUser, @Param("particularPlace") String place, @Param("particularTime") long time);

    @Query(value= "SELECT user_id FROM user_routes_history WHERE device_id = :location AND timestamp  BETWEEN :start AND :end", nativeQuery = true)
    List<String> getAllUsersFromParticularPlaceAndTime(@Param("location")String location, @Param("start")long startDate, @Param("end")long endDate);

    @Query(value = "SELECT user_id, device_id, timestamp FROM user_routes_history WHERE user_id = :user_id", nativeQuery = true)
    List<HistoryTracks> getUserHistory(@Param("user_id")String userId);
}
