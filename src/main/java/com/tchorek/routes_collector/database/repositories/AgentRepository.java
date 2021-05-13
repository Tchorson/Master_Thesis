package com.tchorek.routes_collector.database.repositories;

import com.tchorek.routes_collector.database.model.Agent;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AgentRepository extends CrudRepository<Agent,String> {

    @Query(value = "SELECT COUNT(*) FROM agents WHERE device_name = :deviceName", nativeQuery = true)
    int isDeviceInDB(@Param("deviceName") String phone_number);

    @Query(value = "SELECT device_name, latitude, longitude FROM agents where device_name like '%':deviceName'%'", nativeQuery = true)
    List<Agent> getDevicesFromSpecificArea(@Param("deviceName") String phone_number);
}
