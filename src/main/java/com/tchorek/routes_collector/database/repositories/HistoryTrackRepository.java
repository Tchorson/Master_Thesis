package com.tchorek.routes_collector.database.repositories;

import com.tchorek.routes_collector.database.model.HistoryTracks;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistoryTrackRepository extends CrudRepository<HistoryTracks, String> {

    @Query(value = "INSERT INTO user_routes_history (user_id, device_id, timestamp) " +
            "SELECT user_id, device_id, timestamp FROM user_routes;", nativeQuery = true)
    void transferDailyDataToHistory();
}
