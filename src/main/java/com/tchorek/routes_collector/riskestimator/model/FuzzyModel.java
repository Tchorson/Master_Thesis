package com.tchorek.routes_collector.riskestimator.model;

import com.tchorek.routes_collector.utils.RiskLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;

@Setter
@Getter
@AllArgsConstructor
@ToString
public class FuzzyModel {

    private String user;
    private long deltaAtPlace;
    private long deltaBetweenMeetings;
    private int hierarchyLevel;
    private RiskLevel riskLevel;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FuzzyModel)) return false;
        FuzzyModel that = (FuzzyModel) o;
        return user.equals(that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user);
    }

    @Override
    public String toString() {
        return "user='" + user + '\'' +
                ", time between people [ms]" + deltaAtPlace +
                ", hierarchyLevel=" + hierarchyLevel +
                ", time between meetings [ms]" + deltaBetweenMeetings +
                ", riskLevel=" + riskLevel +
                '\n';
    }
}
