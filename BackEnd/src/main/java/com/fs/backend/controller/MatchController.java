package com.fs.backend.controller;

import com.fs.backend.domain.Player;
import com.fs.backend.dtos.ConnectRequest;
import com.fs.backend.dtos.GameplayRequest;
import com.fs.backend.dtos.MatchDto;
import com.fs.backend.dtos.NewMatchInfoDto;
import com.fs.backend.exceptions.IllegalMovementException;
import com.fs.backend.exceptions.PieceNotFoundException;
import com.fs.backend.service.MatchService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AllArgsConstructor
@Slf4j
@RestController
@RequestMapping("/match")
public class MatchController {

    private MatchService matchService;
    private SimpMessagingTemplate simpMessagingTemplate;

    @PostMapping("/create")
    public ResponseEntity<MatchDto> createGame(@RequestBody Player player) {
        log.info("start game request: {}", player);
        return ResponseEntity.ok(matchService.createMatch(player));
    }

    @PostMapping("/connect")
    public ResponseEntity<MatchDto> connect(@RequestBody ConnectRequest request) {
        log.info("connect request: {}", request);
        return ResponseEntity.ok(matchService.connectMatchById(request.getPlayer(), request.getGameId()));
    }

    @GetMapping("/listNew")
    public ResponseEntity<List<NewMatchInfoDto>> getAllNewMatches() {
        return ResponseEntity.ok(matchService.getAllNewMatchesIds());
    }

    @PostMapping("/connect/random")
    public ResponseEntity<MatchDto> connectRandomGame(@RequestBody Player request) {
        log.info("connect request: {}", request);
        return ResponseEntity.ok(matchService.connectRandomMatch(request));
    }

    @PutMapping("/gameplay")
    public ResponseEntity<MatchDto> gamePlay(@RequestBody GameplayRequest request)
            throws IllegalMovementException, PieceNotFoundException {
        log.info("gameplay: {}", request);
        MatchDto match = matchService.move(request.getMatchId(), request.getPieceToMove(), request.getTarget());

        simpMessagingTemplate.convertAndSend("/topic/game-progress/" + match.getId(), match);

        return ResponseEntity.ok(match);
    }
}
