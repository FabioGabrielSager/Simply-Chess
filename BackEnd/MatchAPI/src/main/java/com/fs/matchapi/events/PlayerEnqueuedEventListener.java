package com.fs.matchapi.events;


import com.fs.matchapi.dtos.MatchDto;
import com.fs.matchapi.dtos.MatchWithPlayer;
import com.fs.matchapi.entities.MatchEntity;
import com.fs.matchapi.entities.PlayerInQueueEntity;
import com.fs.matchapi.model.Match;
import com.fs.matchapi.model.MatchStatus;
import com.fs.matchapi.model.Player;
import com.fs.matchapi.model.pieces.common.PieceColor;
import com.fs.matchapi.repositories.MatchQueueRepository;
import com.fs.matchapi.repositories.MatchRepository;
import lombok.AllArgsConstructor;
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
            MatchWithPlayer responseForHostPlayer = new MatchWithPlayer();
            MatchWithPlayer responseForPlayer2 = new MatchWithPlayer();

            Player hostPlayer = modelMapper.map(playerInQueueEntityOptional.get().getPlayer(), Player.class);
            MatchEntity matchEntity = modelMapper.map(new Match(hostPlayer),
                    MatchEntity.class);

            if (Objects.isNull(matchEntity.getWhitePlayer())) {
                matchEntity.setWhitePlayer(event.getPlayer().getPlayer());
                responseForHostPlayer.setPlayerTeam(PieceColor.BLACK);
                responseForPlayer2.setPlayerTeam(PieceColor.WHITE);
            } else {
                matchEntity.setBlackPlayer(event.getPlayer().getPlayer());
                responseForHostPlayer.setPlayerTeam(PieceColor.WHITE);
                responseForPlayer2.setPlayerTeam(PieceColor.BLACK);
            }

            matchEntity.setStatus(MatchStatus.IN_PROGRESS);

            matchEntity = matchRepository.save(matchEntity);
            MatchDto matchDto = modelMapper.map(matchEntity, MatchDto.class);
            responseForHostPlayer.setMatch(matchDto);
            responseForPlayer2.setMatch(matchDto);

            simpMessagingTemplate.convertAndSend(
                    "/queue/game-queue/" + event.getPlayer().getQueueId(),
                    responseForPlayer2);
            simpMessagingTemplate.convertAndSend(
                    "/queue/game-queue/" + playerInQueueEntityOptional.get().getQueueId(),
                    responseForHostPlayer);

            matchQueueRepository.deleteById(event.getPlayer().getQueueId());
            matchQueueRepository.deleteById(playerInQueueEntityOptional.get().getQueueId());
        }
    }
}
