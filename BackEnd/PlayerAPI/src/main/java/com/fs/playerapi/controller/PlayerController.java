package com.fs.playerapi.controller;

import com.fs.playerapi.dtos.PlayerDto;
import com.fs.playerapi.service.PlayerService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
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

    @MessageMapping("/player.add")
    public PlayerDto addPlayer(@Payload PlayerDto player, SimpMessageHeaderAccessor headerAccessor) {
        Objects.requireNonNull(headerAccessor.getSessionAttributes()).put("playerId", player.getId());
        return playerService.addPlayer(player);
    }

    @DeleteMapping("/delete/{id}")
    public void deletePlayer(@PathVariable String id) {
        playerService.deletePlayer(UUID.fromString(id));
    }

    @GetMapping("/exists/")
    public boolean exists(@RequestParam(required = true) UUID id, @RequestParam(required = true) String name) {
        return playerService.playerExists(PlayerDto.builder().id(id).name(name).build());
    }
}
