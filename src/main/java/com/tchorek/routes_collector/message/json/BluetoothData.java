package com.tchorek.routes_collector.message.json;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@EqualsAndHashCode
public class BluetoothData {

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String user;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String deviceName;

    @JsonCreator
    public BluetoothData(@JsonProperty("user") String user, @JsonProperty("deviceLocation") String deviceName) {
        this.user = user;
        this.deviceName = deviceName;
    }
}
