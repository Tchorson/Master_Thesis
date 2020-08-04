package com.tchorek.routes_collector.database.repositories;

import com.tchorek.routes_collector.database.model.Fugitive;
import org.springframework.data.repository.CrudRepository;

public interface FugitiveRepository extends CrudRepository<Fugitive, String> {
}
