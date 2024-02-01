package com.fs.backend.service;

import com.fs.backend.domain.Player;
import com.fs.backend.domain.pieces.common.Pair;
import com.fs.backend.dtos.MatchDto;
import com.fs.backend.dtos.NewMatchInfoDto;
import com.fs.backend.dtos.PieceRequest;
import com.fs.backend.exceptions.IllegalMovementException;
import com.fs.backend.exceptions.PieceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface MatchService {
    MatchDto createMatch(Player player);
    MatchDto connectMatchById(Player player2, String matchId);
    MatchDto connectRandomMatch(Player player2);
    List<NewMatchInfoDto> getAllNewMatchesIds();
    MatchDto move(String matchId, PieceRequest pieceToMove, Pair target)
            throws IllegalMovementException, PieceNotFoundException;
    MatchDto promoteAPawn(String matchId, PieceRequest pieceToMove, char newPieceSymbol)
            throws IllegalMovementException, PieceNotFoundException;
    MatchDto setMatchAsTied(String matchId);
}
