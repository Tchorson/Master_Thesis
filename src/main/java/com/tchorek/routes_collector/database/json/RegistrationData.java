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
    private long registrationDate;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String latitude;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String longitude;

    @JsonCreator
    public RegistrationData(@JsonProperty("userData") String userData, @JsonProperty("registrationDate") long registrationDate, @JsonProperty("lat") String latitude, @JsonProperty("lng") String longitude) {
        this.userData = userData;
        this.registrationDate = registrationDate;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
