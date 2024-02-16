package com.fs.backend.dtos;

import com.fs.backend.model.Player;
import lombok.Data;
import lombok.Generated;

@Data
@Generated
public class ConnectRequest {
    private Player player;
    private String gameId;
}
