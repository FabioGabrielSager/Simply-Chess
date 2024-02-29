package com.fs.matchapi.service;

import com.fs.matchapi.dtos.MatchDto;
import com.fs.matchapi.dtos.MatchWithPlayer;
import com.fs.matchapi.dtos.PieceRequest;
import com.fs.matchapi.dtos.PlayerInQueueResponse;
import com.fs.matchapi.exceptions.IllegalMovementException;
import com.fs.matchapi.exceptions.PieceNotFoundException;
import com.fs.matchapi.model.Player;
import com.fs.matchapi.model.pieces.common.Pair;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface MatchService {
    MatchWithPlayer createMatch(Player player);
    MatchWithPlayer connectMatchById(Player player2, UUID matchId);
    PlayerInQueueResponse enqueueForMatch(Player player);
    MatchDto move(UUID playerId, UUID matchId, PieceRequest pieceToMove, Pair target)
            throws IllegalMovementException, PieceNotFoundException;
    MatchDto promoteAPawn(UUID playerId, UUID matchId, PieceRequest pieceToMove, char newPieceSymbol)
            throws IllegalMovementException, PieceNotFoundException;
    MatchDto setMatchAsTied(UUID matchId);
}
