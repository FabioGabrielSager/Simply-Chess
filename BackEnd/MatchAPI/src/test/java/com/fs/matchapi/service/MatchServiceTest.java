package com.fs.matchapi.service;

import com.fs.matchapi.config.MappersConfig;
import com.fs.matchapi.dtos.MatchDto;
import com.fs.matchapi.dtos.PieceRequest;
import com.fs.matchapi.dtos.PieceResponse;
import com.fs.matchapi.dtos.PlayerInQueueResponse;
import com.fs.matchapi.entities.MatchEntity;
import com.fs.matchapi.entities.PlayerEntity;
import com.fs.matchapi.entities.PlayerInQueueEntity;
import com.fs.matchapi.exceptions.GameException;
import com.fs.matchapi.exceptions.IllegalMovementException;
import com.fs.matchapi.exceptions.PieceNotFoundException;
import com.fs.matchapi.model.Match;
import com.fs.matchapi.model.MatchStatus;
import com.fs.matchapi.model.Player;
import com.fs.matchapi.model.pieces.Queen;
import com.fs.matchapi.model.pieces.common.Pair;
import com.fs.matchapi.model.pieces.common.Piece;
import com.fs.matchapi.model.pieces.common.PieceColor;
import com.fs.matchapi.repositories.MatchQueueRepository;
import com.fs.matchapi.repositories.MatchRepository;
import com.fs.matchapi.repositories.PlayerRepository;
import com.fs.matchapi.service.imps.MatchServiceImp;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.support.ReflectionSupport;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Import;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@Import(MappersConfig.class)
public class MatchServiceTest {
    @Mock
    private PlayerRepository playerRepository;
    @Mock
    private MatchRepository matchRepository;
    @Mock
    private MatchQueueRepository matchQueueRepository;
    @Mock
    private ApplicationEventPublisher publisher;
    @InjectMocks
    private MatchServiceImp matchServiceImp;

    @Autowired
    private ModelMapper modelMapper;

    @BeforeEach
    void setUp() {
        matchServiceImp.setModelMapper(modelMapper);
    }

    @Test
    @Tag("createMatch")
    public void createMatch_SaveMatch() {
        MatchEntity matchEntity = new MatchEntity();
        matchEntity.setBlackPlayer(new PlayerEntity());
        matchEntity.setWhitePlayer(new PlayerEntity());
        when(matchRepository.save(any())).thenReturn(matchEntity);

        matchServiceImp.createMatch(new Player());

        verify(matchRepository, times(1)).save(any());
    }

    @Test
    @Tag("connectById")
    public void connectMatchById_NotFound() {
        UUID uuid = UUID.randomUUID();
        when(matchRepository.findById(uuid)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> matchServiceImp.connectMatchById(new Player(), uuid));
        verify(matchRepository, times(0)).save(any());
    }

    @Test
    @Tag("connectById")
    public void connectMatchById_GameException() {
        UUID uuid = UUID.randomUUID();
        MatchEntity matchEntity = new MatchEntity();
        matchEntity.setWhitePlayer(new PlayerEntity());
        matchEntity.setBlackPlayer(new PlayerEntity());
        when(matchRepository.findById(uuid)).thenReturn(Optional.of(matchEntity));

        assertThrows(GameException.class, () -> matchServiceImp.connectMatchById(new Player(), uuid));
        verify(matchRepository, times(0)).save(any());
    }

    @Test
    @Tag("connectById")
    public void connectMatchById_SetPlayerBlackPlayer_SetMatchStatus_SaveMatch() {
        UUID uuid = UUID.randomUUID();
        MatchEntity matchEntity = new MatchEntity();
        matchEntity.setWhitePlayer(new PlayerEntity());
        when(matchRepository.findById(uuid)).thenReturn(Optional.of(matchEntity));
        when(matchRepository.save(any())).thenReturn(matchEntity);

        matchServiceImp.connectMatchById(new Player(), uuid);

        verify(matchRepository, times(1)).save(any());
        assertNotNull(matchEntity.getBlackPlayer());
        assertEquals(MatchStatus.IN_PROGRESS, matchEntity.getStatus());
    }

    @Test
    @Tag("connectById")
    public void connectMatchById_SetWhiteBlackPlayer_SetMatchStatus_SaveMatch() {
        UUID uuid = UUID.randomUUID();
        MatchEntity matchEntity = new MatchEntity();
        matchEntity.setBlackPlayer(new PlayerEntity());
        when(matchRepository.findById(uuid)).thenReturn(Optional.of(matchEntity));
        when(matchRepository.save(any())).thenReturn(matchEntity);

        matchServiceImp.connectMatchById(new Player(), uuid);

        verify(matchRepository, times(1)).save(any());
        assertNotNull(matchEntity.getWhitePlayer());
        assertEquals(MatchStatus.IN_PROGRESS, matchEntity.getStatus());
    }

    @Test
    @Tag("enqueueForMatch")
    public void enqueueForMatch_ThereAreNotPlayerInQueue_GetLastPosition_SavePlayerEnqueue() {
        when(matchQueueRepository.getLastPosition()).thenReturn(Optional.empty());
        when(matchQueueRepository.save(any())).thenAnswer((Answer<PlayerInQueueEntity>) invocation -> {
            return invocation.getArgument(0);
        });

        PlayerInQueueResponse result = matchServiceImp.enqueueForMatch(new Player());

        verify(matchQueueRepository, times(1)).save(any());
        verify(publisher, times(1)).publishEvent(any());
        assertEquals(1, result.getPosition());
    }

    @Test
    @Tag("enqueueForMatch")
    public void enqueueForMatch_ThereArePlayerInQueue_GetLastPosition_SavePlayerEnqueue() {
        when(matchQueueRepository.getLastPosition()).thenReturn(Optional.of(3));
        when(matchQueueRepository.save(any())).thenAnswer((Answer<PlayerInQueueEntity>) invocation -> {
            return invocation.getArgument(0);
        });

        PlayerInQueueResponse result = matchServiceImp.enqueueForMatch(new Player());

        verify(matchQueueRepository, times(1)).save(any());
        verify(publisher, times(1)).publishEvent(any());
        assertEquals(3, result.getPosition());
    }

    @Test
    @Tag("move")
    @DisplayName("Move in a new match")
    public void moveTest1() {
        UUID uuid = UUID.randomUUID();
        Player player = Player.builder().id(UUID.randomUUID()).build();
        MatchEntity matchEntity = modelMapper.map(new Match(Player.builder().id(UUID.randomUUID()).build(), player),
                MatchEntity.class);
        matchEntity.setId(uuid);
        matchEntity.setStatus(MatchStatus.NEW);
        PieceRequest pieceRequest = new PieceRequest(PieceColor.WHITE, new Pair(2, 2));

        when(matchRepository.findById(uuid)).thenReturn(Optional.of(matchEntity));

        assertThrows(GameException.class, () ->
                matchServiceImp.move(player, uuid, pieceRequest, new Pair(2, 4)));

        try {
            matchServiceImp.move(player, uuid, pieceRequest, new Pair(2, 4));
        } catch (Exception err) {
            assertEquals("A move cannot be made until another player is connected", err.getMessage());
        }
    }

    @Test
    @Tag("move")
    @DisplayName("Move in a finished game")
    public void moveTest2() {
        UUID uuid = UUID.randomUUID();
        Player player = Player.builder().id(UUID.randomUUID()).build();
        MatchEntity matchEntity = modelMapper.map(new Match(Player.builder().id(UUID.randomUUID()).build(), player),
                MatchEntity.class);
        matchEntity.setId(uuid);
        matchEntity.setStatus(MatchStatus.FINISHED);
        PieceRequest pieceRequest = new PieceRequest(PieceColor.WHITE, new Pair(2, 2));

        when(matchRepository.findById(uuid)).thenReturn(Optional.of(matchEntity));

        assertThrows(GameException.class, () ->
                matchServiceImp.move(player, uuid, pieceRequest, new Pair(2, 4)));

        try {
            matchServiceImp.move(player, uuid, pieceRequest, new Pair(2, 4));
        } catch (Exception err) {
            assertEquals("Cannot make a move in a finished game", err.getMessage());
        }
    }

    @Test
    @Tag("move")
    @DisplayName("Black team try to move in wrong turn")
    public void moveTest3() {
        UUID uuid = UUID.randomUUID();
        Player player = Player.builder().id(UUID.randomUUID()).build();
        MatchEntity matchEntity = modelMapper.map(new Match(Player.builder().id(UUID.randomUUID()).build(), player),
                MatchEntity.class);
        PieceRequest pieceRequest = new PieceRequest(PieceColor.BLACK, new Pair(2, 7));
        matchEntity.setId(uuid);
        when(matchRepository.findById(uuid)).thenReturn(Optional.of(matchEntity));

        assertThrows(IllegalMovementException.class, () ->
                matchServiceImp.move(player, uuid, pieceRequest, new Pair(2, 6)));

        try {
            matchServiceImp.move(player, uuid, pieceRequest, new Pair(2, 6));
        } catch (Exception err) {
            assertEquals("You cannot move in white's turn", err.getMessage());
        }
    }

    @Test
    @Tag("move")
    @DisplayName("White team try to move in wrong turn")
    public void moveTest4() {
        UUID uuid = UUID.randomUUID();
        Player player = Player.builder().id(UUID.randomUUID()).build();
        MatchEntity matchEntity = modelMapper.map(new Match(player,
                        Player.builder().id(UUID.randomUUID()).build()),
                MatchEntity.class);
        PieceRequest pieceRequest = new PieceRequest(PieceColor.WHITE, new Pair(2, 2));
        matchEntity.setId(uuid);
        matchEntity.setWhiteTurn(false);
        when(matchRepository.findById(uuid)).thenReturn(Optional.of(matchEntity));

        assertThrows(IllegalMovementException.class, () ->
                matchServiceImp.move(player, uuid, pieceRequest, new Pair(2, 4)));

        try {
            matchServiceImp.move(player, uuid, pieceRequest, new Pair(2, 4));
        } catch (Exception err) {
            assertEquals("You cannot move in black's turn", err.getMessage());
        }
    }

    @Test
    @Tag("move")
    @DisplayName("As a white team, in white's turn, try to move a black piece")
    public void moveTest5() {
        UUID uuid = UUID.randomUUID();
        Player player = Player.builder().id(UUID.randomUUID()).build();
        MatchEntity matchEntity = modelMapper.map(new Match(player,
                        Player.builder().id(UUID.randomUUID()).build()),
                MatchEntity.class);
        PieceRequest pieceRequest = new PieceRequest(PieceColor.BLACK, new Pair(2, 7));
        matchEntity.setId(uuid);
        when(matchRepository.findById(uuid)).thenReturn(Optional.of(matchEntity));

        assertThrows(IllegalMovementException.class, () ->
                matchServiceImp.move(player, uuid, pieceRequest, new Pair(2, 6)));

        try {
            matchServiceImp.move(player, uuid, pieceRequest, new Pair(2, 6));
        } catch (Exception err) {
            assertEquals("You can't move a black piece", err.getMessage());
        }
    }

    @Test
    @Tag("move")
    @DisplayName("As a black team, in white's turn, try to move a black piece")
    public void moveTest6() {
        UUID uuid = UUID.randomUUID();
        Player player = Player.builder().id(UUID.randomUUID()).build();
        MatchEntity matchEntity = modelMapper.map(new Match(Player.builder().id(UUID.randomUUID()).build(), player),
                MatchEntity.class);
        PieceRequest pieceRequest = new PieceRequest(PieceColor.WHITE, new Pair(2, 2));
        matchEntity.setId(uuid);
        matchEntity.setWhiteTurn(false);
        when(matchRepository.findById(uuid)).thenReturn(Optional.of(matchEntity));

        assertThrows(IllegalMovementException.class, () ->
                matchServiceImp.move(player, uuid, pieceRequest, new Pair(2, 3)));

        try {
            matchServiceImp.move(player, uuid, pieceRequest, new Pair(2, 3));
        } catch (Exception err) {
            assertEquals("You can't move a white piece", err.getMessage());
        }
    }

    @Test
    @Tag("move")
    public void move_PieceNotFound() {
        UUID uuid = UUID.randomUUID();
        Player player = Player.builder().id(UUID.randomUUID()).build();
        MatchEntity matchEntity = modelMapper.map(new Match(player,
                        Player.builder().id(UUID.randomUUID()).build()),
                MatchEntity.class);
        PieceRequest pieceRequest = new PieceRequest(PieceColor.BLACK, new Pair(3, 3));
        matchEntity.setId(uuid);
        when(matchRepository.findById(uuid)).thenReturn(Optional.of(matchEntity));

        assertThrows(PieceNotFoundException.class, () ->
                matchServiceImp.move(player, uuid, pieceRequest, new Pair(3, 4)));
    }

    @Test
    @Tag("move")
    public void move_PieceNotFound2() {
        UUID uuid = UUID.randomUUID();
        Player player = Player.builder().id(UUID.randomUUID()).build();
        MatchEntity matchEntity = modelMapper.map(new Match(player,
                        Player.builder().id(UUID.randomUUID()).build()),
                MatchEntity.class);
        PieceRequest pieceRequest = new PieceRequest(PieceColor.BLACK, new Pair(3, 7));
        matchEntity.setId(uuid);

        matchEntity.getBlackPieces().stream().filter(p -> p.getX() == 3 && p.getY() == 7).findFirst().orElseThrow()
                .setAlive(false);

        when(matchRepository.findById(uuid)).thenReturn(Optional.of(matchEntity));

        assertThrows(PieceNotFoundException.class, () ->
                matchServiceImp.move(player, uuid, pieceRequest, new Pair(3, 2)));
    }

    @Test
    @Tag("move")
    public void moveBlackPiece_SaveMatch() throws IllegalMovementException, PieceNotFoundException {
        UUID uuid = UUID.randomUUID();
        PieceRequest pieceRequest = new PieceRequest(PieceColor.BLACK, new Pair(3, 7));

        Player player = Player.builder().id(UUID.randomUUID()).build();
        MatchEntity matchEntity = modelMapper.map(new Match(
                        Player.builder().id(UUID.randomUUID()).build(), player),
                MatchEntity.class);
        matchEntity.setId(uuid);
        matchEntity.setWhiteTurn(false);

        when(matchRepository.findById(uuid)).thenReturn(Optional.of(matchEntity));
        when(matchRepository.save(any())).thenAnswer((Answer<MatchEntity>) invocation -> {
            return invocation.getArgument(0);
        });

        MatchDto result = matchServiceImp.move(player, uuid, pieceRequest, new Pair(3, 6));

        verify(matchRepository, times(1)).save(any());
        assertFalse(result.isPromotedPawn());
    }

    @Test
    @Tag("move")
    public void moveWhitePiece_SaveMatch() throws IllegalMovementException, PieceNotFoundException {
        UUID uuid = UUID.randomUUID();
        PieceRequest pieceRequest = new PieceRequest(PieceColor.WHITE, new Pair(3, 2));

        Player player = Player.builder().id(UUID.randomUUID()).build();
        MatchEntity matchEntity = modelMapper.map(new Match(player,
                        Player.builder().id(UUID.randomUUID()).build()),
                MatchEntity.class);
        matchEntity.setId(uuid);

        when(matchRepository.findById(uuid)).thenReturn(Optional.of(matchEntity));
        when(matchRepository.save(any())).thenAnswer((Answer<MatchEntity>) invocation -> {
            return invocation.getArgument(0);
        });

        MatchDto result = matchServiceImp.move(player, uuid, pieceRequest, new Pair(3, 3));

        verify(matchRepository, times(1)).save(any());
        assertFalse(result.isPromotedPawn());
    }

    @Test
    @Tag("move")
    public void move_SaveMatch_SetPromotedPawn() throws IllegalMovementException, PieceNotFoundException {
        UUID uuid = UUID.randomUUID();
        PieceRequest pieceRequest = new PieceRequest(PieceColor.BLACK, new Pair(3, 2));

        Player player = Player.builder().id(UUID.randomUUID()).build();
        MatchEntity matchEntity = modelMapper.map(new Match(
                        Player.builder().id(UUID.randomUUID()).build(), player),
                MatchEntity.class);
        matchEntity.setId(uuid);
        matchEntity.setWhiteTurn(false);

        matchEntity.getBlackPieces().stream().filter(p -> p.getX() == 3 && p.getY() == 7)
                .findFirst().orElseThrow().setY(2);

        matchEntity.getWhitePieces().stream().filter(p -> p.getX() == 3 && p.getY() == 1)
                .findFirst().orElseThrow().setAlive(false);


        when(matchRepository.findById(uuid)).thenReturn(Optional.of(matchEntity));
        when(matchRepository.save(any())).thenAnswer((Answer<MatchEntity>) invocation -> {
            return invocation.getArgument(0);
        });

        MatchDto result = matchServiceImp.move(player, uuid, pieceRequest, new Pair(3, 1));

        verify(matchRepository, times(1)).save(any());
        assertTrue(result.isPromotedPawn());
    }

    @Test
    @Tag("promoteAPawn")
    public void promoteABlackPawn_SaveMatch() throws IllegalMovementException, PieceNotFoundException {
        UUID uuid = UUID.randomUUID();
        PlayerEntity blackPlayer = new PlayerEntity();
        blackPlayer.setId(UUID.randomUUID());
        PieceRequest pieceRequest = new PieceRequest(PieceColor.BLACK, new Pair(3, 1));

        MatchEntity matchEntity = modelMapper.map(new Match("", ""),
                MatchEntity.class);
        matchEntity.setId(uuid);
        matchEntity.setWhiteTurn(false);
        matchEntity.setBlackPlayer(blackPlayer);

        matchEntity.getBlackPieces().stream().filter(p -> p.getX() == 3 && p.getY() == 7)
                .findFirst().orElseThrow().setY(1);

        matchEntity.getWhitePieces().stream().filter(p -> p.getX() == 3 && p.getY() == 1)
                .findFirst().orElseThrow().setAlive(false);


        when(matchRepository.findById(uuid)).thenReturn(Optional.of(matchEntity));
        when(matchRepository.save(any())).thenAnswer((Answer<MatchEntity>) invocation -> {
            return invocation.getArgument(0);
        });

        MatchDto result = matchServiceImp.promoteAPawn(modelMapper.map(blackPlayer, Player.class),
                uuid, pieceRequest, 'Q');

        verify(matchRepository, times(1)).save(any());

        List<PieceResponse> blackQueens = result.getBlackPieces().stream().filter(p -> p.getType().equals('Q')).toList();
        assertEquals(2, blackQueens.size());
        assertTrue(blackQueens.stream().anyMatch(p -> p.getPosition().getX() == 3 && p.getPosition().getY() == 1));
    }

    @Test
    @Tag("promoteAPawn")
    public void promoteAWhitePawn_SaveMatch() throws IllegalMovementException, PieceNotFoundException {
        UUID uuid = UUID.randomUUID();
        PlayerEntity whitePlayer = new PlayerEntity();
        whitePlayer.setId(UUID.randomUUID());
        PieceRequest pieceRequest = new PieceRequest(PieceColor.WHITE, new Pair(3, 8));

        MatchEntity matchEntity = modelMapper.map(new Match("", ""),
                MatchEntity.class);
        matchEntity.setId(uuid);
        matchEntity.setWhitePlayer(whitePlayer);

        matchEntity.getWhitePieces().stream().filter(p -> p.getX() == 3 && p.getY() == 2)
                .findFirst().orElseThrow().setY(8);

        matchEntity.getBlackPieces().stream().filter(p -> p.getX() == 3 && p.getY() == 8)
                .findFirst().orElseThrow().setAlive(false);


        when(matchRepository.findById(uuid)).thenReturn(Optional.of(matchEntity));
        when(matchRepository.save(any())).thenAnswer((Answer<MatchEntity>) invocation -> {
            return invocation.getArgument(0);
        });

        MatchDto result = matchServiceImp.promoteAPawn(modelMapper.map(whitePlayer, Player.class),
                uuid, pieceRequest, 'Q');

        verify(matchRepository, times(1)).save(any());

        List<PieceResponse> whiteQueens = result.getWhitePieces().stream().filter(p -> p.getType().equals('Q')).toList();
        assertEquals(2, whiteQueens.size());
        assertTrue(whiteQueens.stream().anyMatch(p -> p.getPosition().getX() == 3 && p.getPosition().getY() == 8));
    }

    @Test
    @Tag("setMatchAsTied")
    public void setMatchAsTied_SaveMatch() {
        UUID uuid = UUID.randomUUID();

        MatchEntity matchEntity = modelMapper.map(new Match("", ""),
                MatchEntity.class);
        matchEntity.setId(uuid);

        when(matchRepository.findById(uuid)).thenReturn(Optional.of(matchEntity));
        when(matchRepository.save(any())).thenAnswer((Answer<MatchEntity>) invocation -> {
            return invocation.getArgument(0);
        });

        MatchDto result = matchServiceImp.setMatchAsTied(uuid);

        verify(matchRepository, times(1)).save(any());

        assertEquals(MatchStatus.TIED, result.getStatus());
    }

    @Test
    @Tag("findMatchbyId")
    public void findMatchById_NotFound() {
        UUID uuid = UUID.randomUUID();

        when(matchRepository.findById(uuid)).thenReturn(Optional.empty());

        Method findMatcById = ReflectionSupport.findMethod(MatchServiceImp.class, "findMatchById",
                UUID.class).orElseThrow();

        assertThrows(EntityNotFoundException.class, () -> ReflectionSupport
                .invokeMethod(findMatcById, matchServiceImp, uuid));
    }

    @Test
    @Tag("findMatchbyId")
    public void findMatchById_Successful() {
        UUID uuid = UUID.randomUUID();

        when(matchRepository.findById(uuid)).thenReturn(Optional.of(new MatchEntity()));

        Method findMatcById = ReflectionSupport.findMethod(MatchServiceImp.class, "findMatchById",
                UUID.class).orElseThrow();

        assertNotNull(ReflectionSupport
                .invokeMethod(findMatcById, matchServiceImp, uuid));
    }
}
