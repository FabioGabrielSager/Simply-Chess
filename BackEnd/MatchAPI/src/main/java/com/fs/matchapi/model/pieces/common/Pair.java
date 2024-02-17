package com.fs.matchapi.model.pieces.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Pair implements Cloneable {
    private int x;
    private int y;

    public boolean isValidWithinBounds(int topLimit, int bottomLimit) {
        return x <= topLimit && x > bottomLimit && y <= topLimit && y > bottomLimit;
    }

    @Override
    public Pair clone() {
        try {
            return (Pair) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
