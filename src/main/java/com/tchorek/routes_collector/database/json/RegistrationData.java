package com.tchorek.routes_collector.database.json;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RegistrationData {

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String userData;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private long date;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private long latitude;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private long longitude;

    @JsonCreator
    public RegistrationData(@JsonProperty("userData") String userData, @JsonProperty("registrationDate") long date, @JsonProperty("lat") long latitude, @JsonProperty("lng") long longitude) {
        this.userData = userData;
        this.date = date;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
