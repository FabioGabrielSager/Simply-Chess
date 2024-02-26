package com.fs.matchapi.dtos;

import com.fs.matchapi.model.MatchStatus;
import com.fs.matchapi.model.pieces.common.Piece;
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
    private String finishReason;
    private boolean isWhiteTurn;
    private List<PieceResponse> whitePieces;
    private List<PieceResponse> blackPieces;
    private String whitePlayer;
    private String blackPlayer;
    private String winner;
    private boolean isPromotedPawn;
}
