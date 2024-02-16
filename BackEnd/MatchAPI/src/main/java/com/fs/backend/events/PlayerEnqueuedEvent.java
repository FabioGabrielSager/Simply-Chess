package com.fs.backend.events;

import com.fs.backend.entities.PlayerInQueueEntity;
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
