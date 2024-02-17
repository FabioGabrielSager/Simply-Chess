package com.fs.matchapi.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Player {
    private UUID id;
    private String name;
    public Player(String playerName) {
        this.name = playerName;
    }
}
