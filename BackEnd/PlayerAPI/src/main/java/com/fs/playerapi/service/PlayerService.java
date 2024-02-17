package com.fs.playerapi.service;

import com.fs.playerapi.dtos.PlayerDto;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface PlayerService {
    PlayerDto addPlayer(PlayerDto player);
    void deletePlayer(UUID playerId);
    boolean playerExists(PlayerDto playerDto);
}
