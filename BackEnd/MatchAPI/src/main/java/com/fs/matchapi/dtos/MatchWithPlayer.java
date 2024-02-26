package com.fs.matchapi.dtos;

import com.fs.matchapi.model.Player;
import com.fs.matchapi.model.pieces.common.PieceColor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MatchWithPlayer {
    private MatchDto match;
    private Player player;
    private PieceColor playerTeam;
}
