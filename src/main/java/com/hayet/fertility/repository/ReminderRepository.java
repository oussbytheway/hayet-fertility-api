package com.hayet.fertility.repository;

import com.hayet.fertility.domain.Reminder;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Reminder entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ReminderRepository extends JpaRepository<Reminder, Long> {

    @Query("select count(c) > 0 from Client c where c.id = :clientId")
    boolean existsClientById(@Param("clientId") Long clientId);

}
