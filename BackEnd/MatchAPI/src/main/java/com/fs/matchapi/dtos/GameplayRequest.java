package com.fs.matchapi.dtos;

import com.fs.matchapi.model.Player;
import com.fs.matchapi.model.pieces.common.Pair;
import lombok.Generated;
import lombok.Getter;

@Getter
@Generated
public class GameplayRequest {
    private String matchId;
    private PieceRequest pieceToMove;
    private Pair target;
}
