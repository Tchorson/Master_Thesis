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
    private long returnDate;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String targetPlace;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Float latitude;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Float longitude;

    @JsonCreator
    public RegistrationData(@JsonProperty("userData") String userData, @JsonProperty("targetPlace") String targetPlace, @JsonProperty("registrationDate") long date, @JsonProperty("registrationDate") long returnDate, @JsonProperty("lat") Float latitude, @JsonProperty("lng") Float longitude) {
        this.userData = userData;
        this.date = date;
        this.returnDate = returnDate;
        this.latitude = latitude;
        this.longitude = longitude;
        this.targetPlace = targetPlace;
    }

    @Override
    public String toString() {
        return "Registration user " + userData +" " + Timer.getFullDate(date) + ", return date: " + Timer.getFullDate(returnDate) + " lat: " + latitude +" lng: " + longitude;
    }
}
