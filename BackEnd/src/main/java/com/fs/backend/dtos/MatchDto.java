package com.fs.backend.dtos;

import com.fs.backend.domain.MatchStatus;
import com.fs.backend.domain.Player;
import com.fs.backend.domain.pieces.common.Piece;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MatchDto {
    private UUID id;
    private MatchStatus status;
    private boolean isWhiteTurn;
    private List<Piece> whitePieces;
    private List<Piece> blackPieces;
    private Player whitePlayer;
    private Player blackPlayer;
    private Player winner;
    private boolean isPromotedPawn;
}
