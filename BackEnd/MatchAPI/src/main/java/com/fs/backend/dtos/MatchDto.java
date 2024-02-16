package com.fs.backend.dtos;

import com.fs.backend.model.MatchStatus;
import com.fs.backend.model.pieces.common.Piece;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Generated;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Generated
public class MatchDto {
    private UUID id;
    private MatchStatus status;
    private boolean isWhiteTurn;
    private List<Piece> whitePieces;
    private List<Piece> blackPieces;
    private String whitePlayer;
    private String blackPlayer;
    private String winner;
    private boolean isPromotedPawn;
}
