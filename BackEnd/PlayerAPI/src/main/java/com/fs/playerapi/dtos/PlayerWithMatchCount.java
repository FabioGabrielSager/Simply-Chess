package com.fs.playerapi.dtos;

import com.fs.playerapi.entities.PlayerEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PlayerWithMatchCount {
    private PlayerEntity player;
    private long count;
}
