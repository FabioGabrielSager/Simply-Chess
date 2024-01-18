package com.fs.backend.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Player {
    private Long id;
    private String name;

    public Player(String playerName) {
        this.name = playerName;
    }
}
