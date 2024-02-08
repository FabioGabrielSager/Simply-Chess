package com.fs.backend.dtos;

import com.fs.backend.model.Player;
import com.fs.backend.model.pieces.common.Pair;
import lombok.Generated;
import lombok.Getter;

@Getter
@Generated
public class GameplayRequest {
    private String matchId;
    private Player player;
    private PieceRequest pieceToMove;
    private Pair target;
}
