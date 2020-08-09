package com.tchorek.routes_collector.utils;

import java.time.Instant;

public class Timer {

    public static long getCurrentTimeInSeconds(){
        return Instant.now().getEpochSecond();
    }
}
