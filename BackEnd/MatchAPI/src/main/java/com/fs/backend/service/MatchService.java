package com.fs.backend.service;

import com.fs.backend.dtos.MatchDto;
import com.fs.backend.dtos.PieceRequest;
import com.fs.backend.dtos.PlayerInQueueResponse;
import com.fs.backend.exceptions.IllegalMovementException;
import com.fs.backend.exceptions.PieceNotFoundException;
import com.fs.backend.model.Player;
import com.fs.backend.model.pieces.common.Pair;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface MatchService {
    MatchDto createMatch(Player player);
    MatchDto connectMatchById(Player player2, UUID matchId);
    PlayerInQueueResponse enqueueForMatch(Player player);
    MatchDto move(Player player, UUID matchId, PieceRequest pieceToMove, Pair target)
            throws IllegalMovementException, PieceNotFoundException;
    MatchDto promoteAPawn(Player player, UUID matchId, PieceRequest pieceToMove, char newPieceSymbol)
            throws IllegalMovementException, PieceNotFoundException;
    MatchDto setMatchAsTied(UUID matchId);
}
