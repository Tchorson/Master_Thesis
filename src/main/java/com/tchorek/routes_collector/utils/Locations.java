package com.tchorek.routes_collector.utils;

import lombok.Getter;

@Getter
enum Locations {
    PARK("PARK"),
    YARD("YARD");

    private String location;

    Locations(String location){
        this.location = location;
    }

    boolean isValid(String input){
        for(Locations location : Locations.values()){
            if (input.equals(location.name())){
                return true;
            }
        }
        return false;
    }

}
