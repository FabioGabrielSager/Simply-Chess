package com.fs.matchapi.dtos;

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
public class MatchWithPlayerTeam {
    private MatchDto match;
    private PieceColor playerTeam;
}
