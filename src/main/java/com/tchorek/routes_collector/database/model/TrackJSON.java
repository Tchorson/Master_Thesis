package com.tchorek.routes_collector.database.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TrackJSON {

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String number;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String location;

    @JsonCreator
    public TrackJSON(@JsonProperty("phoneNumber") String number,@JsonProperty("userLocation") String location) {
        this.number = number;
        this.location = location;
    }
}
