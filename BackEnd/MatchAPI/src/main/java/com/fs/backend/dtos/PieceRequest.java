package com.fs.backend.dtos;

import com.fs.backend.model.pieces.common.Pair;
import com.fs.backend.model.pieces.common.PieceColor;
import lombok.Generated;

@Generated
public record PieceRequest(PieceColor color, Pair position) {
}
