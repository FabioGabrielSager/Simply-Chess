package com.fs.backend.dtos;

import com.fs.backend.domain.pieces.common.Pair;
import lombok.Data;

@Data
public class GameplayRequest {
    private String matchId;
    PieceRequest pieceToMove;
    Pair target;
}
