package com.fs.matchapi.config;

import com.fs.matchapi.dtos.MatchDto;
import com.fs.matchapi.entities.MatchEntity;
import com.fs.matchapi.entities.PlayerEntity;
import com.fs.matchapi.model.MatchStatus;
import com.fs.matchapi.repositories.MatchQueueRepository;
import com.fs.matchapi.repositories.MatchRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Component
@AllArgsConstructor
@Slf4j
@Import(MappersConfig.class)
public class WebSocketEventListener {

    private SimpMessagingTemplate messagingTemplate;
    private MatchQueueRepository matchQueueRepository;
    private MatchRepository matchRepository;
    private ModelMapper modelMapper;

    @EventListener
    @Transactional
    public void handleWebSocketDisconnectEvent(SessionDisconnectEvent event) {
        log.info("Listen to websocket disconnect event");
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        Objects.requireNonNull(headerAccessor.getSessionAttributes());

        Optional<MatchEntity> matchEntityOptional = matchRepository
                .findById((UUID) headerAccessor.getSessionAttributes().get("matchId"));

        if(matchEntityOptional.isEmpty()) {
            return;
        }

        MatchEntity matchEntity = matchEntityOptional.get();
        String disconnectionReason = "";

        if(matchEntity.getWhitePlayer() != null && matchEntity.getBlackPlayer() != null) {
            PlayerEntity disconnectedPlayer = matchEntity.getWhitePlayer().getId()
                    == headerAccessor.getSessionAttributes().get("playerId")
                    ? matchEntity.getWhitePlayer() : matchEntity.getBlackPlayer();

            matchEntity.setWinner(
                    disconnectedPlayer.equals(matchEntity.getWhitePlayer())
                            ? matchEntity.getBlackPlayer() : matchEntity.getWhitePlayer());
            disconnectionReason = "Player: " + disconnectedPlayer.getName() + " left the game";
        }

        MatchDto matchDto = modelMapper.map(matchEntity, MatchDto.class);
        matchDto.setStatus(MatchStatus.FINISHED);
        matchDto.setFinishReason(disconnectionReason);

        messagingTemplate.convertAndSend("/queue/game-progress/" + matchDto.getId(), matchDto);

        matchRepository.delete(matchEntity);
    }

    @EventListener
    public void handleWebSocketSubscribeEvent(SessionSubscribeEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String destination = headerAccessor.getDestination();
        if(Objects.requireNonNull(destination).startsWith("/queue/game-progress/")) {
            if(!Objects.requireNonNull(headerAccessor.getSessionAttributes()).containsKey("matchId")) {
                headerAccessor
                        .getSessionAttributes()
                        .put("matchId", UUID.fromString(destination.substring(21)));
            }
        }

        if(Objects.requireNonNull(destination).startsWith("/queue/game-queue/")) {
            if(!Objects.requireNonNull(headerAccessor.getSessionAttributes()).containsKey("playerId")) {
                String playerID = matchQueueRepository
                        .findById(UUID.fromString(destination.substring(18)))
                        .orElseThrow().getPlayer().getId().toString();

                headerAccessor.getSessionAttributes().put("playerId", playerID);
            }
        }
    }
}
