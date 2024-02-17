package com.fs.matchapi.events;

import com.fs.matchapi.entities.PlayerInQueueEntity;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class PlayerEnqueuedEvent extends ApplicationEvent {
    PlayerInQueueEntity player;


    public PlayerEnqueuedEvent(Object source, PlayerInQueueEntity player) {
        super(source);
        this.player = player;
    }
}
