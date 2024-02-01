package com.fs.backend.repositories;

import com.fs.backend.domain.MatchStatus;
import com.fs.backend.entities.MatchEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MatchRepository extends JpaRepository<MatchEntity, String> {

    Optional<MatchEntity> findFirstByStatus(MatchStatus status);
    List<MatchEntity> findAllByStatus(MatchStatus status);
}
