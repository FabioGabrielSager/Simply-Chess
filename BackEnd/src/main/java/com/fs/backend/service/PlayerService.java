package com.fs.backend.service;

import com.fs.backend.model.Player;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface PlayerService {
    Player addPlayer(Player player);
    void deletePlayer(UUID playerId);
}
