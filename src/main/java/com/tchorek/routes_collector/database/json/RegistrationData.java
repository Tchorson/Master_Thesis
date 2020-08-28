package com.tchorek.routes_collector.database.json;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tchorek.routes_collector.utils.Timer;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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
        return "Registration user " + userData +" " + Timer.getFullDate(date) + " lat: " + latitude +" lng: " + longitude;
    }
}
