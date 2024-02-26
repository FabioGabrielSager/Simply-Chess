package com.fs.playerapi.repositories;

import com.fs.playerapi.entities.PlayerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PlayerRepository extends JpaRepository<PlayerEntity, UUID> {
    Optional<PlayerEntity> findByIdAndName(UUID id, String name);

    @Transactional
    @Modifying
    @Query("DELETE FROM PlayerEntity p WHERE p.status = 'OFFLINE' AND p.matches IS EMPTY")
    void deleteDisconnectedPlayersWithNoMatches();
}
