package com.fs.backend.repositories;

import com.fs.backend.entities.MatchEntity;
import com.fs.backend.model.MatchStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MatchRepository extends JpaRepository<MatchEntity, UUID> {

    Optional<MatchEntity> findFirstByStatus(MatchStatus status);
}
