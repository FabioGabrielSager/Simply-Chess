package com.fs.backend.service.imps;

import com.fs.backend.domain.Match;
import com.fs.backend.domain.MatchStatus;
import com.fs.backend.domain.Player;
import com.fs.backend.domain.pieces.Pawn;
import com.fs.backend.domain.pieces.PieceFactory;
import com.fs.backend.domain.pieces.common.Pair;
import com.fs.backend.domain.pieces.common.Piece;
import com.fs.backend.domain.pieces.common.PieceColor;
import com.fs.backend.dtos.MatchDto;
import com.fs.backend.dtos.NewMatchInfoDto;
import com.fs.backend.dtos.PieceRequest;
import com.fs.backend.entities.MatchEntity;
import com.fs.backend.entities.PlayerEntity;
import com.fs.backend.exceptions.GameException;
import com.fs.backend.exceptions.IllegalMovementException;
import com.fs.backend.exceptions.PieceNotFoundException;
import com.fs.backend.repositories.MatchRepository;
import com.fs.backend.service.MatchService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MatchServiceImp implements MatchService {

    // TODO: ESTO VA PARA EL SERVICE DEL FRONTEND
    private final HashMap<Character, Integer> COL_BY_ALGEBRAIC_SYMBOL = new HashMap<>(
            Map.ofEntries(
                    new AbstractMap.SimpleImmutableEntry<>('a', 1),
                    new AbstractMap.SimpleImmutableEntry<>('b', 2),
                    new AbstractMap.SimpleImmutableEntry<>('c', 3),
                    new AbstractMap.SimpleImmutableEntry<>('d', 4),
                    new AbstractMap.SimpleImmutableEntry<>('e', 5),
                    new AbstractMap.SimpleImmutableEntry<>('f', 6),
                    new AbstractMap.SimpleImmutableEntry<>('g', 7),
                    new AbstractMap.SimpleImmutableEntry<>('h', 8)
            ));

    private final MatchRepository matchRepository;
    private final ModelMapper modelMapper;

    @Override
    public MatchDto createMatch(Player player) {
        Match match = new Match(player);

        MatchEntity matchEntity = modelMapper.map(match, MatchEntity.class);
        matchEntity.setId(UUID.randomUUID().toString());

        matchEntity = matchRepository.save(matchEntity);

        return modelMapper.map(matchEntity, MatchDto.class);
    }

    @Override
    public MatchDto connectMatchById(Player player2, String matchId) {
        Optional<MatchEntity> matchEntityOptional = matchRepository.findById(matchId);

        if (matchEntityOptional.isEmpty()) {
            throw new EntityNotFoundException("Game with id " + matchId + " not founded");
        }

        MatchEntity match = matchEntityOptional.get();

        if (Objects.nonNull(match.getWhitePlayer()) && Objects.nonNull(match.getBlackPlayer())) {
            throw new GameException("The game is no longer valid, there are already two players playing");
        }

        if (Objects.isNull(match.getWhitePlayer())) {
            match.setWhitePlayer(modelMapper.map(player2, PlayerEntity.class));
        } else {
            match.setBlackPlayer(modelMapper.map(player2, PlayerEntity.class));
        }

        match.setStatus(MatchStatus.IN_PROGRESS);

        MatchEntity savedMatch = matchRepository.save(modelMapper.map(match, MatchEntity.class));

        return modelMapper.map(savedMatch, MatchDto.class);
    }

    @Override
    public MatchDto connectRandomMatch(Player player2) {
        Optional<MatchEntity> newMatchOptional = matchRepository.findFirstByStatus(MatchStatus.NEW);

        if (newMatchOptional.isEmpty()) {
            throw new EntityNotFoundException("There is no available game right now");
        }

        MatchEntity match = newMatchOptional.get();

        if (Objects.isNull(match.getWhitePlayer())) {
            match.setWhitePlayer(modelMapper.map(player2, PlayerEntity.class));
        } else {
            match.setBlackPlayer(modelMapper.map(player2, PlayerEntity.class));
        }

        match.setStatus(MatchStatus.IN_PROGRESS);

        return modelMapper.map(matchRepository.save(match), MatchDto.class);
    }

    @Override
    public List<NewMatchInfoDto> getAllNewMatchesIds() {
        return matchRepository.findAllByStatus(MatchStatus.NEW).stream().map(m ->
                new NewMatchInfoDto(m.getId(),
                        modelMapper.map(m.getWhitePlayer() == null ? m.getBlackPlayer() : m.getWhitePlayer(),
                                Player.class))).toList();
    }

    @Override
    public MatchDto move(String matchId, PieceRequest pieceToMove, Pair target)
            throws IllegalMovementException, PieceNotFoundException {

        Match match = findMatchById(matchId);
        Piece piece = pieceToMove.color() == PieceColor.BLACK ?
                match.getBlackPieces().stream().filter(p ->
                                p.getPosition().getX() == pieceToMove.position().getX()
                                        && p.getPosition().getY() == pieceToMove.position().getY()).findFirst()
                        .orElseThrow(() -> new PieceNotFoundException("There is no piece in that position"))
                :
                match.getWhitePieces().stream().filter(p ->
                                p.getPosition().getX() == pieceToMove.position().getX()
                                        && p.getPosition().getY() == pieceToMove.position().getY()).findFirst()
                        .orElseThrow(() -> new PieceNotFoundException("There is no piece in that position"));

        match.move(piece, target);

        MatchEntity savedMatch = matchRepository.save(modelMapper.map(match, MatchEntity.class));
        MatchDto matchDto = modelMapper.map(savedMatch, MatchDto.class);

        if (piece instanceof Pawn) {
            if (((Pawn) piece).isPromoted(match.getBOARD_LENGHT())) {
                matchDto.setPromotedPawn(true);
            }
        }

        return matchDto;
    }

    @Override
    public MatchDto promoteAPawn(String matchId, PieceRequest promotedPawn, char newPieceSymbol)
            throws IllegalMovementException, PieceNotFoundException {

        Match match = findMatchById(matchId);
        Pawn piece = promotedPawn.color() == PieceColor.BLACK ?
                (Pawn) match.getBlackPieces().stream().filter(p ->
                                p instanceof Pawn
                                        && p.getPosition().getX() == promotedPawn.position().getX()
                                        && p.getPosition().getY() == promotedPawn.position().getY()).findFirst()
                        .orElseThrow(() -> new PieceNotFoundException("There is no pawn in that position"))
                :
                (Pawn) match.getWhitePieces().stream().filter(p ->
                                p instanceof Pawn
                                        && p.getPosition().getX() == promotedPawn.position().getX()
                                        && p.getPosition().getY() == promotedPawn.position().getY()).findFirst()
                        .orElseThrow(() -> new PieceNotFoundException("There is no pawn in that position"));

        Piece newPiece = PieceFactory.create(Character.toUpperCase(newPieceSymbol), promotedPawn.color());

        match.promoteAPawn(piece, newPiece);

        MatchEntity savedMatch = matchRepository.save(modelMapper.map(match, MatchEntity.class));

        return modelMapper.map(savedMatch, MatchDto.class);
    }

    @Override
    public MatchDto setMatchAsTied(String matchId) {
        Match match = findMatchById(matchId);
        match.setStatus(MatchStatus.TIED);
        MatchEntity savedMatch = matchRepository.save(modelMapper.map(match, MatchEntity.class));

        return modelMapper.map(savedMatch, MatchDto.class);
    }

    private Match findMatchById(String matchId) {
        Optional<MatchEntity> matchEntityOptional = matchRepository.findById(matchId);

        if (matchEntityOptional.isEmpty()) {
            throw new EntityNotFoundException("Game with id " + matchId + " not founded");
        }

        return modelMapper.map(matchEntityOptional.get(), Match.class);
    }
}
