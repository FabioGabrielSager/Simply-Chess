package com.fs.backend.dtos;

import com.fs.backend.domain.pieces.common.Pair;
import com.fs.backend.domain.pieces.common.PieceColor;

public record PieceRequest(PieceColor color, Pair position) {
}
