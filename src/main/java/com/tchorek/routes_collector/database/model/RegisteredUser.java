package com.tchorek.routes_collector.database.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Setter
@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
@Entity(name = "registered_people")
public class RegisteredUser {

    @Id
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Column(name = "user_id", nullable = false)
    private String phoneNumber;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Column(name = "password", nullable = false)
    private String password;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "token")
    @JsonInclude()
    private String token;

    @Column(name = "timestamp")
    @JsonInclude()
    private Long timestamp;

    @JsonCreator
    public RegisteredUser(@JsonProperty("userData") String userData, @JsonProperty("password") String password, @JsonProperty("email") String email, @JsonProperty("token") String token, @JsonProperty("timestamp") Long timestamp){
        this.phoneNumber = userData;
        this.password = password;
        this.email = email;
        this.token = token;
        this.timestamp = timestamp;
    }
}
