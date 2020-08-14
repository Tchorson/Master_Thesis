package com.tchorek.routes_collector.utils;

import java.time.Instant;

public class Timer {

    public static long getCurrentTimeInSeconds(){
        return Instant.now().getEpochSecond();
    }

    public static Instant getFullDate(long unixTimestamp){ return Instant.ofEpochSecond(unixTimestamp);}
}
