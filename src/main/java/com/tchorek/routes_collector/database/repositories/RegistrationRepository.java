package com.tchorek.routes_collector.database.repositories;

import com.tchorek.routes_collector.database.model.Registration;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;

@Repository
public interface RegistrationRepository extends CrudRepository<Registration, String> {

    @Query(value = "SELECT user_id, walk_date, latitude, longitude, approved FROM user_registrations WHERE approved IS NULL", nativeQuery = true)
    List<Registration> getAllNewRegistrations();

    @Query(value = "SELECT user_id, walk_date, latitude, longitude, approved FROM user_registrations WHERE approved = true", nativeQuery = true)
    List<Registration> getAllVerifiedRegistrations();

    @Query(value = "SELECT DISTINCT user_id FROM user_registrations WHERE approved = true", nativeQuery = true)
    Set<String> getAllApprovedUsers();

    @Modifying
    @Transactional
    @Query(value= "DELETE FROM user_registrations WHERE approved IS NOT NULL" ,nativeQuery = true)
    void deleteVerifiedRegistrations();

    @Query(value = "SELECT COUNT(*) FROM user_registrations WHERE user_id = :user_id AND approved = true", nativeQuery = true)
    int isUserRegistered(@Param("user_id") String userId);
}
