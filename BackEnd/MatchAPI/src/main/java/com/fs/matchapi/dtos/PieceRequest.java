package com.fs.matchapi.dtos;

import com.fs.matchapi.model.pieces.common.Pair;
import com.fs.matchapi.model.pieces.common.PieceColor;
import lombok.Generated;

@Generated
public record PieceRequest(PieceColor color, Pair position) {
}
