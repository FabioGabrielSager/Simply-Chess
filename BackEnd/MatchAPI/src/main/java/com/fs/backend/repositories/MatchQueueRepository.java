package com.fs.backend.repositories;

import com.fs.backend.entities.PlayerInQueueEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MatchQueueRepository extends JpaRepository<PlayerInQueueEntity, UUID> {

    @Query("select p from PlayerInQueueEntity as p " +
            "where p.position = (select min(subp.position) from PlayerInQueueEntity as subp)")
    Optional<PlayerInQueueEntity> getFirstPlayer();

    @Query("select p from PlayerInQueueEntity as p order by p.position asc")
    List<PlayerInQueueEntity> findAllOrderByPosition();

    @Query("select max(p.position) from PlayerInQueueEntity as p")
    Optional<Integer> getLastPosition();

    Optional<PlayerInQueueEntity> findByPosition(Integer position);

    void deleteById(UUID id);
}
