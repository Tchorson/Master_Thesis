package com.tchorek.routes_collector.database.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tchorek.routes_collector.utils.Timer;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Setter
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

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Column(name = "return_date", nullable = false)
    private long returnDate;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Column(name = "target_place", nullable = false)
    private String targetPlace;

    @JsonInclude()
    @Column(name = "latitude", nullable = false, precision = 9, scale = 6)
    private Float latitude;

    @JsonInclude()
    @Column(name = "longitude", nullable = false, precision = 9, scale = 6)
    private Float longitude;

    @JsonInclude()
    @Column(name = "approved")
    private Boolean approved;

    @Override
    public String toString() {
        return "Registration " + phoneNumber + " " + Timer.getFullDate(walkTimestamp) + " "
                + Timer.getFullDate(returnDate) + " " + latitude + ", " + longitude+ " "+ approved +"\n";
    }

    @JsonCreator
    public Registration(@JsonProperty("userData") String userData, @JsonProperty("targetPlace") String targetPlace, @JsonProperty("walkDate") long date, @JsonProperty("returnDate") long returnDate, @JsonProperty("lat") Float latitude, @JsonProperty("lng") Float longitude, @JsonProperty("approved") Boolean isApproved ) {
        this.phoneNumber = userData;
        this.walkTimestamp = date;
        this.returnDate = returnDate;
        this.latitude = latitude;
        this.longitude = longitude;
        this.approved = isApproved;
        this.targetPlace = targetPlace;
    }
}
