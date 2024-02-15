package com.fs.backend.controller;

import com.fs.backend.model.Player;
import com.fs.backend.service.PlayerService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping("/player")
public class PlayerController {
    private PlayerService playerService;

    @PostMapping("/add")
    public Player addPlayer(@RequestBody Player player) {
        return playerService.addPlayer(player);
    }

    @DeleteMapping("/delete/{id}")
    public void deletePlayer(@PathVariable String id) {
        playerService.deletePlayer(UUID.fromString(id));
    }
}
