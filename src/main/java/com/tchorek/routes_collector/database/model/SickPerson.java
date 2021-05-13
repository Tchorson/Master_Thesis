package com.tchorek.routes_collector.database.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
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
@Entity(name = "sick_people")
public class SickPerson {

    @Id
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Column(name = "user_id", nullable = false)
    private String phoneNumber;

    @Column(name = "is_reported")
    private boolean isReported;

    @Column(name = "report_date")
    private Long reportDate;

    @JsonCreator
    public SickPerson(@JsonProperty("userData") String userData, @JsonProperty("reported") Boolean isReported, @JsonProperty("reportDate") Long reportDate){
        this.phoneNumber = userData;
        this.isReported = isReported;
        this.reportDate = reportDate;
    }
}
