package com.fs.backend.events;


import com.fs.backend.dtos.MatchDto;
import com.fs.backend.entities.MatchEntity;
import com.fs.backend.entities.PlayerInQueueEntity;
import com.fs.backend.model.Match;
import com.fs.backend.model.MatchStatus;
import com.fs.backend.model.Player;
import com.fs.backend.repositories.MatchQueueRepository;
import com.fs.backend.repositories.MatchRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

@Component
@AllArgsConstructor
@Setter
public class PlayerEnqueuedEventListener implements ApplicationListener<PlayerEnqueuedEvent> {

    private MatchRepository matchRepository;
    private MatchQueueRepository matchQueueRepository;
    private ModelMapper modelMapper;
    private SimpMessagingTemplate simpMessagingTemplate;

    @Override
    public void onApplicationEvent(PlayerEnqueuedEvent event) {
        Optional<PlayerInQueueEntity> playerInQueueEntityOptional = matchQueueRepository.findByPosition(
                event.getPlayer().getPosition() - 1);

        if (playerInQueueEntityOptional.isPresent()) {
            Player hostPlayer = modelMapper.map(playerInQueueEntityOptional.get().getPlayer(), Player.class);
            MatchEntity matchEntity = modelMapper.map(new Match(hostPlayer),
                    MatchEntity.class);

            if (Objects.isNull(matchEntity.getWhitePlayer())) {
                matchEntity.setWhitePlayer(event.getPlayer().getPlayer());
            } else {
                matchEntity.setBlackPlayer(event.getPlayer().getPlayer());
            }

            matchEntity.setStatus(MatchStatus.IN_PROGRESS);

            matchEntity = matchRepository.save(matchEntity);
            MatchDto matchDto = modelMapper.map(matchEntity, MatchDto.class);

            simpMessagingTemplate.convertAndSend(
                    "queue/game-queue/" + event.getPlayer().getQueueId(),
                    matchDto);
            simpMessagingTemplate.convertAndSend(
                    "queue/game-queue/" + playerInQueueEntityOptional.get().getQueueId(),
                    matchDto);

            matchQueueRepository.deleteById(event.getPlayer().getQueueId());
            matchQueueRepository.deleteById(playerInQueueEntityOptional.get().getQueueId());
        }
    }
}
