package com.tchorek.routes_collector.utils;

import lombok.Getter;

@Getter
enum DeviceType {
    RPI("RPI");

    private String deviceType;

    DeviceType(String deviceType){
        this.deviceType = deviceType;
    }

    boolean isValid(String input){
        for(DeviceType type : DeviceType.values()){
            if (input.equals(type.name())){
                return true;
            }
        }
        return false;
    }
}
