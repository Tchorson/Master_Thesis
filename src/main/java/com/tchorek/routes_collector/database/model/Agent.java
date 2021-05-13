package com.tchorek.routes_collector.database.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Entity
@ToString
@NoArgsConstructor
@Table(name = "agents")
public class Agent {

    @Id
    @Column(name = "device_name", nullable = false)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String deviceName;

    @JsonInclude()
    @Column(name = "latitude", nullable = false, precision = 9, scale = 6)
    private Float latitude;

    @JsonInclude()
    @Column(name = "longitude", nullable = false, precision = 9, scale = 6)
    private Float longitude;

    @JsonCreator
    public Agent(@JsonProperty("deviceName") String deviceName, @JsonProperty("lat") Float latitude, @JsonProperty("lng") Float longitude) {
        this.deviceName = deviceName;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
