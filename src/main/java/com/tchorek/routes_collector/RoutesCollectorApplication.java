package com.tchorek.routes_collector;

import com.tchorek.routes_collector.database.repositories.TrackRepository;
import com.tchorek.routes_collector.database.model.Track;
import com.tchorek.routes_collector.database.service.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.List;

@SpringBootApplication
@EnableScheduling
public class RoutesCollectorApplication implements CommandLineRunner {

    @Autowired
    TrackRepository trackRepository;

    @Autowired
    DatabaseService databaseService;

    public static void main(String[] args) {
        SpringApplication.run(RoutesCollectorApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        Iterable<Track> systemlist = trackRepository.findAll();
        for(Track systemmodel:systemlist){
            System.out.println(systemmodel.toString());
        }
        List<String> users = databaseService.getListOfUsersByLocationAndTime("RPI_PARK_02",1595280968);
        users.forEach(System.out::println);
        System.out.println("--------------------");
        List<String> metPeople = databaseService.getListOfUsersWhoMetUserRecently("555456789",1595280959,1595283959);
        metPeople.forEach(System.out::println);
    }
}
