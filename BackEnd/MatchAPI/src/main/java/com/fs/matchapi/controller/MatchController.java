package com.fs.matchapi.controller;

import com.fs.matchapi.dtos.ConnectRequest;
import com.fs.matchapi.dtos.GameplayRequest;
import com.fs.matchapi.dtos.MatchDto;
import com.fs.matchapi.dtos.MatchWithPlayer;
import com.fs.matchapi.dtos.MatchWithPlayerTeam;
import com.fs.matchapi.dtos.PlayerInQueueResponse;
import com.fs.matchapi.dtos.PromoteRequest;
import com.fs.matchapi.exceptions.IllegalMovementException;
import com.fs.matchapi.exceptions.PieceNotFoundException;
import com.fs.matchapi.model.Player;
import com.fs.matchapi.model.pieces.common.PieceColor;
import com.fs.matchapi.service.MatchService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;
import java.util.UUID;

@AllArgsConstructor
@Slf4j
@Controller
@RequestMapping("/match")
@CrossOrigin(origins = "http://localhost:4200")
public class MatchController {

    private MatchService matchService;
    private SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/create")
    @SendToUser(value = "/queue/reply", broadcast = false)
    @Transactional
    public MatchWithPlayerTeam createGame(@Payload Player request, SimpMessageHeaderAccessor headerAccessor) {
        log.info("start game request: {}", request);
        MatchWithPlayer match = matchService.createMatch(request);

        Objects.requireNonNull(headerAccessor.getSessionAttributes());

        headerAccessor.getSessionAttributes().put("matchId", match.getMatch().getId());
        headerAccessor.getSessionAttributes().put("playerId", match.getPlayer().getId());

        return MatchWithPlayerTeam.builder().playerTeam(match.getPlayerTeam()).match(match.getMatch()).build();
    }

    @MessageMapping("/connect")
    @SendToUser(value = "/queue/reply", broadcast = false)
    @Transactional
    public PieceColor connect(@Payload ConnectRequest request, SimpMessageHeaderAccessor headerAccessor) {
        log.info("connect request: {}", request);
        MatchWithPlayer match =
                matchService.connectMatchById(request.getPlayer(), UUID.fromString(request.getGameId()));

        Objects.requireNonNull(headerAccessor.getSessionAttributes());

        headerAccessor.getSessionAttributes().put("matchId", match.getMatch().getId());
        headerAccessor.getSessionAttributes().put("playerId", match.getPlayer().getId());

        simpMessagingTemplate.convertAndSend("/queue/game-progress/" + match.getMatch().getId(),
                match.getMatch());

        return match.getPlayerTeam();
    }

    @PostMapping("/connect/matchesQueue")
    public ResponseEntity<PlayerInQueueResponse> connectToMatchesQueue(@RequestBody Player request) {
        log.info("connect request: {}", request);
        return ResponseEntity.ok(matchService.enqueueForMatch(request));
    }

    @MessageMapping("/gameplay")
    public void gamePlay(@RequestBody GameplayRequest request, SimpMessageHeaderAccessor headerAccessor)
            throws IllegalMovementException, PieceNotFoundException {
        log.info("gameplay: {}", request);

        UUID playerID = (UUID) Objects.requireNonNull(headerAccessor.getSessionAttributes()).get("playerId");

        MatchDto match = matchService.move(playerID,
                UUID.fromString(request.getMatchId()), request.getPieceToMove(), request.getTarget());

        simpMessagingTemplate.convertAndSend("/queue/game-progress/" + match.getId(), match);
    }

    @MessageMapping("/promote")
    public void promote(@Payload PromoteRequest request, SimpMessageHeaderAccessor headerAccessor)
            throws IllegalMovementException, PieceNotFoundException {
        log.info("Promote: {}", request);

        UUID playerID = (UUID) Objects.requireNonNull(headerAccessor.getSessionAttributes()).get("playerId");

        MatchDto match = matchService.promoteAPawn(playerID, UUID.fromString(request.getMatchId()),
                request.getPawnToPromote(),
                request.getNewPieceSymbol());

        simpMessagingTemplate.convertAndSend("/queue/game-progress/" + match.getId(), match);

    }

    @PutMapping("/tieMatch")
    public ResponseEntity<MatchDto> tieMatch(@PathVariable String matchId) {
        log.info("Tie match: {}", matchId);
        MatchDto match = matchService.setMatchAsTied(UUID.fromString(matchId));

        simpMessagingTemplate.convertAndSend("/queue/game-progress/" + match.getId(), match);

        return ResponseEntity.ok(match);
    }
}
