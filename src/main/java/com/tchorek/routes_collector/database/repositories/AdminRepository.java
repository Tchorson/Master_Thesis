package com.tchorek.routes_collector.database.repositories;

import com.tchorek.routes_collector.database.model.Admin;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface AdminRepository extends CrudRepository<Admin,String> {

    @Query(value = "SELECT token FROM admins WHERE login = :login AND password = :password", nativeQuery = true)
    String getUserToken(@Param("login") String login, @Param("password") String password);

    @Transactional
    @Modifying
    @Query(value = "UPDATE admins SET token = :token, timestamp = :timestamp WHERE login = :login AND password = :password", nativeQuery = true)
    void createUserSession(@Param("login") String login, @Param("password") String password, @Param("token") String token, @Param("timestamp") long timestamp);

    @Transactional
    @Modifying
    @Query(value = "UPDATE admins SET token = NULL, timestamp = NULL WHERE token = :token", nativeQuery = true)
    void removeUserSession(@Param("token") String token);

    @Transactional
    @Modifying
    @Query(value = "UPDATE admins SET timestamp = :timestamp WHERE token = :token", nativeQuery = true)
    void updateUserSessionTimestamp(@Param("timestamp") long timestamp, @Param("token") String token);

    @Query(value = "SELECT login FROM admins WHERE token = :token LIMIT 1", nativeQuery = true)
    String findUserSession(@Param("token") String token);

    @Transactional
    @Modifying
    @Query(value = "UPDATE admins SET token = NULL, timestamp = NULL WHERE :currentTime > timestamp  ", nativeQuery = true)
    void removeInactiveSessions(@Param("currentTime") long currentTime);
}
