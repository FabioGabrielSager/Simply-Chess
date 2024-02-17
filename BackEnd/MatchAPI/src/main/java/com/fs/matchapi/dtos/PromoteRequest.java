package com.fs.matchapi.dtos;

import com.fs.matchapi.model.Player;
import lombok.Generated;
import lombok.Getter;

@Getter
@Generated
public class PromoteRequest {
    private Player player;
    private String matchId;
    private PieceRequest pawnToPromote;
    private char newPieceSymbol;
}
