package com.fs.backend.dtos;

import com.fs.backend.domain.Player;
import lombok.Data;

@Data
public class ConnectRequest {
    private Player player;
    private String gameId;
}
