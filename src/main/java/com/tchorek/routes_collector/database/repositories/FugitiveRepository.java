package com.tchorek.routes_collector.database.repositories;

import com.tchorek.routes_collector.database.model.Fugitive;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

public interface FugitiveRepository extends CrudRepository<Fugitive, String> {

    @Query(value = "SELECT user_id, latitude, longitude, escape_date, is_reported FROM fugitives WHERE is_reported IS FALSE", nativeQuery = true)
    List<Fugitive> getAllUnreportedFugitives();

    @Query(value = "UPDATE fugitives SET is_reported = TRUE WHERE user_id IN :ids", nativeQuery = true)
    @Modifying
    @Transactional
    void markFugitivesAsReported(@Param("ids") List<String> ids);
}
