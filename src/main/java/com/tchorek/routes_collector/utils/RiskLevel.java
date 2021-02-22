package com.tchorek.routes_collector.utils;

import lombok.Getter;

@Getter
public enum RiskLevel {
    UNKNOWN(-1.0),
    LACKING(0.0),
    LOW(1.0),
    MEDIUM(2.0),
    HIGH(3.0);

    private double risk;

    RiskLevel(double risk){
        this.risk = risk;
    }

    public double getValue() {
        return risk;
    }
}
