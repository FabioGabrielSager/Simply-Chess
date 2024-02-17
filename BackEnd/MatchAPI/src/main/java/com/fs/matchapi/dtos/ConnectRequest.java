package com.fs.matchapi.dtos;

import com.fs.matchapi.model.Player;
import lombok.Data;
import lombok.Generated;

@Data
@Generated
public class ConnectRequest {
    private Player player;
    private String gameId;
}
