package com.tchorek.routes_collector.database.repositories;

import com.tchorek.routes_collector.database.model.SickPerson;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface SickPeopleRepository extends CrudRepository<SickPerson, String> {

    @Query(value = "SELECT COUNT(*) FROM sick_people", nativeQuery = true)
    int countPeople();

    @Query(value = "SELECT user_id, is_reported, report_date FROM sick_people WHERE is_reported = false", nativeQuery = true)
    List<SickPerson> collectUnreportedPeople();

    @Query(value = "UPDATE sick_people SET is_reported = TRUE WHERE is_reported = TRUE AND report_date < :time", nativeQuery = true)
    @Modifying
    @Transactional
    void removeReportedPeople(@Param("time")long time);

    @Query(value = "UPDATE sick_people SET is_reported = TRUE, report_date = :time WHERE user_id IN :ids;", nativeQuery = true)
    @Modifying
    @Transactional
    void markUnreportedAsReported(@Param("ids") List<String> ids, @Param("time") long currentTime);
}
