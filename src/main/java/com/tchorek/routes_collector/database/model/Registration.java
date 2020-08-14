package com.tchorek.routes_collector.database.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tchorek.routes_collector.utils.Timer;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
@Entity(name = "user_registrations")
public class Registration {

    @Id
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Column(name = "user_id", nullable = false)
    private String phoneNumber;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Column(name = "walk_date", nullable = false)
    private long walkTimestamp;

    @JsonInclude()
    @Column(name = "latitude", nullable = false)
    private Float latitude;

    @JsonInclude()
    @Column(name = "longitude", nullable = false)
    private Float longitude;

    @JsonInclude()
    @Column(name = "approved")
    private Boolean approved;

    @Override
    public String toString() {
        return "Registration " + phoneNumber + " " + Timer.getFullDate(walkTimestamp) + " "
                + latitude + ", " + longitude+ " "+ approved +"\n";
    }

    @JsonCreator
    public Registration(@JsonProperty("userData") String userData, @JsonProperty("walkDate") long date, @JsonProperty("lat") Float latitude, @JsonProperty("lng") Float longitude, @JsonProperty("approved") Boolean isApproved ) {
        this.phoneNumber = userData;
        this.walkTimestamp = date;
        this.latitude = latitude;
        this.longitude = longitude;
        this.approved = isApproved;
    }
}
