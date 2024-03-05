package com.fs.matchapi.config;

import com.fs.matchapi.dtos.MatchDto;
import com.fs.matchapi.entities.MatchEntity;
import com.fs.matchapi.entities.PlayerEntity;
import com.fs.matchapi.entities.PlayerInQueueEntity;
import com.fs.matchapi.events.PlayerEnqueuedEvent;
import com.fs.matchapi.model.MatchStatus;
import com.fs.matchapi.model.pieces.common.PieceColor;
import com.fs.matchapi.repositories.MatchQueueRepository;
import com.fs.matchapi.repositories.MatchRepository;
import com.fs.matchapi.repositories.PlayerRepository;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
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
public class WebSocketEventListener implements ApplicationEventPublisherAware {

    private SimpMessagingTemplate messagingTemplate;
    private MatchQueueRepository matchQueueRepository;
    private PlayerRepository playerRepository;
    private MatchRepository matchRepository;
    private ModelMapper modelMapper;
    private ApplicationEventPublisher publisher;

    @EventListener
    @Transactional
    public void handleWebSocketDisconnectEvent(SessionDisconnectEvent event) {
        log.info("Listen to websocket disconnect event");

        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        Objects.requireNonNull(headerAccessor.getSessionAttributes());

        PlayerEntity playerEntity =
                playerRepository.findById((UUID) headerAccessor.getSessionAttributes().get("playerId")).orElseThrow();

        matchQueueRepository.deleteByPlayer(playerEntity);

        if(headerAccessor.getSessionAttributes().get("matchId") != null) {
            Optional<MatchEntity> matchEntityOptional = matchRepository
                    .findById((UUID) headerAccessor.getSessionAttributes().get("matchId"));

            if(matchEntityOptional.isEmpty()) {
                return;
            }

            MatchEntity matchEntity = matchEntityOptional.get();

            if(!(matchEntity.getStatus().equals(MatchStatus.FINISHED_BY_WIN) ||
                    matchEntity.getStatus().equals(MatchStatus.TIED))) {
                if (matchEntity.getWhitePlayer() != null && matchEntity.getBlackPlayer() != null) {
                    PlayerEntity disconnectedPlayer = matchEntity.getWhitePlayer().getId()
                            == headerAccessor.getSessionAttributes().get("playerId")
                            ? matchEntity.getWhitePlayer() : matchEntity.getBlackPlayer();

                    matchEntity.setWinner(
                            disconnectedPlayer.equals(matchEntity.getWhitePlayer())
                                    ? PieceColor.BLACK : PieceColor.WHITE);

                    matchEntity.setStatus(MatchStatus.FINISHED_BY_ABANDONMENT);
                    matchRepository.save(matchEntity);
                }
            }

            MatchDto matchDto = modelMapper.map(matchEntity, MatchDto.class);
            messagingTemplate.convertAndSend("/queue/game-progress/" + matchDto.getId(), matchDto);

        }
    }

    @EventListener
    public void handleWebSocketSubscribeEvent(SessionSubscribeEvent event) {
        log.info("Listen to websocket subscribe event");
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
                PlayerInQueueEntity playerInQueueEntity = matchQueueRepository
                        .findById(UUID.fromString(destination.substring(18))).orElseThrow();

                UUID playerID = playerInQueueEntity.getPlayer().getId();

                headerAccessor.getSessionAttributes().put("playerId", playerID);
                publisher.publishEvent(new PlayerEnqueuedEvent(this, playerInQueueEntity));
            }
        }
    }

    @Override
    public void setApplicationEventPublisher(@NonNull ApplicationEventPublisher applicationEventPublisher) {
        this.publisher = applicationEventPublisher;
    }
}
