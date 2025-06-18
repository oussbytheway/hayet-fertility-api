package com.hayet.fertility.repository;

import com.hayet.fertility.domain.MessageInstance;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the MessageInstance entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MessageInstanceRepository extends JpaRepository<MessageInstance, Long> {}
