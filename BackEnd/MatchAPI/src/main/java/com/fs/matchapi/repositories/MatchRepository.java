package com.fs.matchapi.repositories;

import com.fs.matchapi.entities.MatchEntity;
import com.fs.matchapi.model.MatchStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MatchRepository extends JpaRepository<MatchEntity, UUID> {

    Optional<MatchEntity> findFirstByStatus(MatchStatus status);
}
