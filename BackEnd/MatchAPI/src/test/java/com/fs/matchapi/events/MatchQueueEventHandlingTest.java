package com.fs.matchapi.events;

import com.fs.matchapi.dtos.MatchDto;
import com.fs.matchapi.dtos.MatchWithPlayerTeam;
import com.fs.matchapi.entities.MatchEntity;
import com.fs.matchapi.entities.PlayerEntity;
import com.fs.matchapi.entities.PlayerInQueueEntity;
import com.fs.matchapi.repositories.MatchQueueRepository;
import com.fs.matchapi.repositories.MatchRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class MatchQueueEventHandlingTest {

    @Autowired
    private ApplicationEventPublisher publisher;
    @Autowired
    private ModelMapper modelMapper;

    @Mock
    private MatchQueueRepository matchQueueRepository;
    @Mock
    private MatchRepository matchRepository;
    @Mock
    private SimpMessagingTemplate simpMessagingTemplate;
    @InjectMocks
    private PlayerEnqueuedEventListener eventListener;

    @BeforeEach
    public void setUp() {
        eventListener.setModelMapper(modelMapper);
    }

    @Test
    public void onPlayerEnqueued_ThereIsNoOtherPlayerEnqueued() {
        PlayerInQueueEntity playerToEnqueue = new PlayerInQueueEntity();
        playerToEnqueue.setPosition(1);

        publisher.publishEvent(new PlayerEnqueuedEvent(this, playerToEnqueue));

        verify(simpMessagingTemplate, times(0)).convertAndSend(any());
        verify(matchRepository, times(0)).save(any());
        verify(matchQueueRepository, times(0)).deleteById(any());
    }

    @Test
    public void onPlayerEnqueued_ThereIsAnotherPlayerEnqueued() {
        PlayerInQueueEntity playerToEnqueue = new PlayerInQueueEntity();
        playerToEnqueue.setPosition(2);
        playerToEnqueue.setPlayer(new PlayerEntity());
        PlayerInQueueEntity playerEnqueued = new PlayerInQueueEntity();
        playerEnqueued.setPosition(1);
        playerEnqueued.setPlayer(new PlayerEntity());

        PlayerEnqueuedEvent event = new PlayerEnqueuedEvent(this, playerToEnqueue);

        when(matchQueueRepository.findByPosition(any()))
                .thenReturn(Optional.of(playerEnqueued));
        when(matchRepository.save(any())).thenReturn(new MatchEntity());

        eventListener.onApplicationEvent(event);

        verify(simpMessagingTemplate, times(2))
                .convertAndSend(anyString(), isA(MatchWithPlayerTeam.class));
        verify(matchRepository, times(1)).save(any());
        verify(matchQueueRepository, times(2)).deleteById(any());
    }
}
