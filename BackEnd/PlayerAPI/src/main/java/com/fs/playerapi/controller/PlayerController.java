package com.fs.playerapi.controller;

import com.fs.playerapi.dtos.PlayerDto;
import com.fs.playerapi.service.PlayerService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Objects;
import java.util.UUID;

@AllArgsConstructor
@Controller
public class PlayerController {
    private PlayerService playerService;

    @MessageMapping("/add")
    @SendToUser("/queue/reply")
    public PlayerDto addPlayer(@Payload String player, SimpMessageHeaderAccessor headerAccessor) {
        PlayerDto response = playerService.addPlayer(player);
        Objects.requireNonNull(headerAccessor.getSessionAttributes()).put("playerId", response.getId());
        return response;
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @DeleteMapping("/delete/{id}")
    public void deletePlayer(@PathVariable String id) {
        playerService.deletePlayer(UUID.fromString(id));
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/exists/")
    public ResponseEntity<Boolean> exists(@RequestParam @Nullable UUID id, @RequestParam @Nullable String name) {
        if(Objects.isNull(id) || name.isEmpty()) {
            return ResponseEntity.ok(false);
        }
        return ResponseEntity.ok(playerService.playerExists(PlayerDto.builder().id(id).name(name).build()));
    }
}
