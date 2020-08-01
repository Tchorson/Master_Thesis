package com.tchorek.routes_collector.message.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class BluetoothData {

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String user;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String location;

    @JsonCreator
    public BluetoothData(@JsonProperty("user") String user, @JsonProperty("deviceLocation") String location) {
        this.user = user;
        this.location = location;
    }
}
