package com.tchorek.routes_collector.database.json;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ServerDataJSON {

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String userData;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private long startDate;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private long stopDate;

    @JsonCreator
    public ServerDataJSON(@JsonProperty("userData") String userData, @JsonProperty("startDate") long startDate, @JsonProperty("stopDate") long stopDate) {
        this.userData = userData;
        this.startDate = startDate;
        this.stopDate = stopDate;
    }
}
