package com.tchorek.routes_collector.database.json;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class BluetoothDataJSON {

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String user;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String location;

    @JsonCreator
    public BluetoothDataJSON(@JsonProperty("user") String user, @JsonProperty("userLocation") String location) {
        this.user = user;
        this.location = location;
    }
}
