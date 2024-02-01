package com.fs.backend.repositories;

import com.fs.backend.entities.PieceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

//TODO: DELETE THIS REPOSITORY ISN'T USED
@Repository
public interface PieceRepository extends JpaRepository<PieceEntity, Long> {
}
