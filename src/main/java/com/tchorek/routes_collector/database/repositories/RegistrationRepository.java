package com.tchorek.routes_collector.database.repositories;

import com.tchorek.routes_collector.database.model.Registration;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegistrationRepository extends CrudRepository<Registration, String> {
}
