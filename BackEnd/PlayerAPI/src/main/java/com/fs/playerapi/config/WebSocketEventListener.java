package com.fs.playerapi.config;

import com.fs.playerapi.entities.PlayerEntity;
import com.fs.playerapi.entities.PlayerStatus;
import com.fs.playerapi.repositories.PlayerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Objects;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListener {
    private final PlayerRepository playerRepository;

    @EventListener
    @Transactional
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        UUID playerId = (UUID) Objects.requireNonNull(headerAccessor.getSessionAttributes()).get("playerId");

        PlayerEntity player = playerRepository.findById(playerId).orElseThrow();

        player.setStatus(PlayerStatus.OFFLINE);

        playerRepository.save(player);
    }
}

