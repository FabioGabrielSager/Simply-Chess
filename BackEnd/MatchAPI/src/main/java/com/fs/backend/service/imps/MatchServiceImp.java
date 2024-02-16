package com.fs.backend.service.imps;

import com.fs.backend.dtos.PlayerInQueueResponse;
import com.fs.backend.entities.PlayerInQueueEntity;
import com.fs.backend.events.PlayerEnqueuedEvent;
import com.fs.backend.model.Match;
import com.fs.backend.model.MatchStatus;
import com.fs.backend.model.Player;
import com.fs.backend.model.pieces.Pawn;
import com.fs.backend.model.pieces.PieceFactory;
import com.fs.backend.model.pieces.common.Pair;
import com.fs.backend.model.pieces.common.Piece;
import com.fs.backend.model.pieces.common.PieceColor;
import com.fs.backend.dtos.MatchDto;
import com.fs.backend.dtos.PieceRequest;
import com.fs.backend.entities.MatchEntity;
import com.fs.backend.entities.PlayerEntity;
import com.fs.backend.exceptions.GameException;
import com.fs.backend.exceptions.IllegalMovementException;
import com.fs.backend.exceptions.PieceNotFoundException;
import com.fs.backend.repositories.MatchRepository;
import com.fs.backend.repositories.MatchQueueRepository;
import com.fs.backend.service.MatchService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
@Setter
public class MatchServiceImp implements MatchService, ApplicationEventPublisherAware {

    private MatchRepository matchRepository;
    private MatchQueueRepository matchQueueRepository;
    private ModelMapper modelMapper;
    private ApplicationEventPublisher publisher;

    @Override
    public MatchDto createMatch(Player player) {
        Match match = new Match(player);

        MatchEntity matchEntity = modelMapper.map(match, MatchEntity.class);

        matchEntity = matchRepository.save(matchEntity);

        MatchDto matchDto = modelMapper.map(matchEntity, MatchDto.class);

        return matchDto;
    }

    @Override
    public MatchDto connectMatchById(Player player2, UUID matchId) {
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
    public PlayerInQueueResponse enqueueForMatch(Player player) {
        PlayerInQueueEntity playerInQueueEntity = new PlayerInQueueEntity();

        playerInQueueEntity.setPlayer(modelMapper.map(player, PlayerEntity.class));

        Optional<Integer> matchPositionOptional = matchQueueRepository.getLastPosition();

        if(matchPositionOptional.isPresent()) {
            playerInQueueEntity.setPosition(matchPositionOptional.get());
        } else {
            playerInQueueEntity.setPosition(1);
        }

        playerInQueueEntity = matchQueueRepository.save(playerInQueueEntity);
        publisher.publishEvent(new PlayerEnqueuedEvent(this, playerInQueueEntity));

        return modelMapper.map(playerInQueueEntity, PlayerInQueueResponse.class);
    }

    @Override
    public MatchDto move(Player player, UUID matchId, PieceRequest pieceToMove, Pair target)
            throws IllegalMovementException, PieceNotFoundException {

        Match match = findMatchById(matchId);

        if (match.getStatus().equals(MatchStatus.FINISHED) || match.getStatus().equals(MatchStatus.TIED)) {
            throw new GameException("Cannot make a move in a finished game");
        }

        if (match.getStatus().equals(MatchStatus.NEW)) {
            throw new GameException("A move cannot be made until another player is connected");
        }

        List<Piece> piecesInThatPosition = pieceToMove.color() == PieceColor.BLACK ?
                match.getBlackPieces().stream().filter(p ->
                        p.getPosition().getX() == pieceToMove.position().getX()
                                && p.getPosition().getY() == pieceToMove.position().getY()).toList()
                :
                match.getWhitePieces().stream().filter(p ->
                        p.getPosition().getX() == pieceToMove.position().getX()
                                && p.getPosition().getY() == pieceToMove.position().getY()).toList();

        if (piecesInThatPosition.isEmpty()) {
            throw new PieceNotFoundException("There is no piece in that position");
        }

        Piece piece = piecesInThatPosition.stream().filter(p -> p.isAlive() == true).findFirst().orElseThrow(() ->
                new PieceNotFoundException("There is no piece in that position"));

        if(match.isWhiteTurn()) {
            if (player.getId() != match.getWhitePlayer().getId()) {
                throw new IllegalMovementException("You cannot move in white's turn");
            }
            else if(piece.getColor().equals(PieceColor.BLACK)) {
                throw new IllegalMovementException("You can't move a black piece");
            }
        }
        else {
            if (player.getId() != match.getBlackPlayer().getId()) {
                throw new IllegalMovementException("You cannot move in black's turn");
            }
            else if(piece.getColor().equals(PieceColor.WHITE)) {
                throw new IllegalMovementException("You can't move a white piece");
            }
        }

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
    public MatchDto promoteAPawn(Player player, UUID matchId, PieceRequest promotedPawn, char newPieceSymbol)
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

        if(player.getId() == match.getWhitePlayer().getId() && piece.getColor().equals(PieceColor.BLACK)) {
            throw new IllegalMovementException("You can't promote a black piece");
        }

        if(player.getId() == match.getBlackPlayer().getId() && piece.getColor().equals(PieceColor.WHITE)) {
            throw new IllegalMovementException("You can't promote a white piece");
        }

        match.promoteAPawn(piece, newPiece);

        MatchEntity savedMatch = matchRepository.save(modelMapper.map(match, MatchEntity.class));

        return modelMapper.map(savedMatch, MatchDto.class);
    }

    @Override
    public MatchDto setMatchAsTied(UUID matchId) {
        Match match = findMatchById(matchId);
        match.setStatus(MatchStatus.TIED);
        MatchEntity savedMatch = matchRepository.save(modelMapper.map(match, MatchEntity.class));

        return modelMapper.map(savedMatch, MatchDto.class);
    }

    private Match findMatchById(UUID matchId) {
        Optional<MatchEntity> matchEntityOptional = matchRepository.findById(matchId);

        if (matchEntityOptional.isEmpty()) {
            throw new EntityNotFoundException("Game with id " + matchId + " not founded");
        }

        return modelMapper.map(matchEntityOptional.get(), Match.class);
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.publisher = applicationEventPublisher;
    }
}
