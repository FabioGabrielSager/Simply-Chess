package com.fs.matchapi.dtos;

import com.fs.matchapi.model.pieces.common.Pair;
import com.fs.matchapi.model.pieces.common.PieceColor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class PieceResponse {
    private Long id;
    private Pair position;
    private PieceColor color;
    private boolean isAlive;
    private Character type;
}
