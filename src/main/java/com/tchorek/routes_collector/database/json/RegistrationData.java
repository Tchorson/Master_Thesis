package com.tchorek.routes_collector.database.json;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Setter
@Getter
@EqualsAndHashCode
public class RegistrationData {

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String userData;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private long date;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Float latitude;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Float longitude;

    @JsonCreator
    public RegistrationData(@JsonProperty("userData") String userData, @JsonProperty("registrationDate") long date, @JsonProperty("lat") Float latitude, @JsonProperty("lng") Float longitude) {
        this.userData = userData;
        this.date = date;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "RegistrationData{" +
                "userData='" + userData + '\'' +
                ", date=" + Instant.ofEpochSecond(date)  +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
