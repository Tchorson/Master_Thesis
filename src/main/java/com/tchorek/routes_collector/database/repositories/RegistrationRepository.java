package com.tchorek.routes_collector.database.repositories;

import com.tchorek.routes_collector.database.model.HistoryTracks;
import com.tchorek.routes_collector.database.model.Registration;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface RegistrationRepository extends CrudRepository<Registration, String> {

    @Query(value = "SELECT user_id, walk_date, latitude, longitude, approved FROM user_registrations WHERE approved IS NULL", nativeQuery = true)
    List<HistoryTracks> getAllNewRegistrations();

    @Query(value = "SELECT user_id, walk_date, latitude, longitude, approved FROM user_registrations WHERE approved = true", nativeQuery = true)
    HistoryTracks getAllApprovedRegistrations();

    @Query(value = "SELECT DISTINCT user_id FROM user_registrations WHERE approved = true", nativeQuery = true)
    Set<String> getAllApprovedUsers();

    @Query(value = "DELETE FROM user_registrations WHERE approved = false", nativeQuery = true)
    HistoryTracks deleteAllUnapprovedRegistrations();
}
