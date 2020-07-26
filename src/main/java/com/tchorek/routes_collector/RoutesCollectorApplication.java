package com.tchorek.routes_collector;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RoutesCollectorApplication{

    public static void main(String[] args) {
        SpringApplication.run(RoutesCollectorApplication.class, args);
    }
}
