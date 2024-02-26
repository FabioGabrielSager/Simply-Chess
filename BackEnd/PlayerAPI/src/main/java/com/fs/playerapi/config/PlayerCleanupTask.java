package com.fs.playerapi.config;

import com.fs.playerapi.repositories.PlayerRepository;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@AllArgsConstructor
public class PlayerCleanupTask {
    private PlayerRepository playerRepository;

    @Scheduled(timeUnit = TimeUnit.HOURS, fixedDelay = 6)
    public void cleanupDisconnectedPlayers() {
        playerRepository.deleteDisconnectedPlayersWithNoMatches();
    }
}
