package com.tchorek.routes_collector.database.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.lang.NonNull;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Entity
@ToString
@NoArgsConstructor
@Table(name = "admins")
public class Admin {
    @Id
    @Column(name = "login", nullable = false)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String login;

    @Column(name = "password", nullable = false)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String password;

    @Column(name = "token")
    @JsonInclude()
    private String token;

    @Column(name = "timestamp")
    @JsonInclude()
    private Long timestamp;

    @JsonCreator
    public Admin(@JsonProperty("login") @NonNull String login, @JsonProperty("password") @NonNull String password, @JsonProperty("userData") String token, @JsonProperty("timestamp") Long timestamp) {
        this.login = login;
        this.password = password;
        this.token = token;
        this.timestamp = timestamp;
    }
}
