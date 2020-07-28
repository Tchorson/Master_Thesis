package com.tchorek.routes_collector.database.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TrackTimeJSON {

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String number;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private long startDate;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private long stopDate;

    @JsonCreator
    public TrackTimeJSON(@JsonProperty("phoneNumber") String number, @JsonProperty("startDate") long startDate, @JsonProperty("stopDate") long stopDate) {
        this.number = number;
        this.startDate = startDate;
        this.stopDate = stopDate;
    }
}
