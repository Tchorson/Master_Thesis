package com.tchorek.routes_collector.database.repositories;

import com.tchorek.routes_collector.database.model.RegisteredUser;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface UserRegistrationRepository extends CrudRepository<RegisteredUser, String> {

    @Query(value = "DELETE FROM registered_people WHERE user_id = :id AND email = :email AND token = :token", nativeQuery = true)
    @Modifying
    @Transactional
    void removeUserFromDatabase(@Param("id") String id, @Param("email") String email, @Param("token") String token);

    @Transactional
    @Modifying
    @Query(value = "UPDATE registered_people SET token = NULL, timestamp = NULL WHERE token = :token", nativeQuery = true)
    void removeUserSession(@Param("token") String token);

    @Query(value = "SELECT token FROM registered_people WHERE email = :login AND password = :password", nativeQuery = true)
    String getUserToken(@Param("login") String login, @Param("password") String password);

    @Query(value = "SELECT email FROM registered_people WHERE token = :token LIMIT 1", nativeQuery = true)
    String findUserSession(@Param("token") String token);

    @Transactional
    @Modifying
    @Query(value = "UPDATE registered_people SET timestamp = :timestamp WHERE token = :token", nativeQuery = true)
    void updateUserSessionTimestamp(@Param("timestamp") long timestamp, @Param("token") String token);

    @Transactional
    @Modifying
    @Query(value = "UPDATE registered_people SET token = :token, timestamp = :timestamp WHERE email = :login", nativeQuery = true)
    void createUserSession(@Param("login") String login,  @Param("token") String token, @Param("timestamp") long timestamp);
}
