package dev.ninjdai.splatcube.gui;

import org.jetbrains.annotations.NotNull;

public class Mask {

    /**
     * A two-dimensional array of booleans indicating which slots are 'enabled' and which ones are 'disabled'. This
     * two-dimensional array is constructed in a row-major order fashion.
     */
    private final boolean[][] mask;

    public Mask(@NotNull String... mask) {
        this.mask = new boolean[mask.length][mask.length == 0 ? 0 : mask[0].length()];

        for (int row = 0; row < mask.length; row++) {
            int length = mask[row].length();

            if (length != this.mask[row].length) {
                throw new IllegalArgumentException("Lengths of each string should be equal");
            }

            for (int column = 0; column < length; column++) {
                char character = mask[row].charAt(column);

                if (character == '0') {
                    this.mask[row][column] = false;
                } else if (character == '1') {
                    this.mask[row][column] = true;
                } else {
                    throw new IllegalArgumentException("Strings may only contain '0' and '1'");
                }
            }
        }
    }

    public int getWidth() {
        return mask.length == 0 ? 0 : mask[0].length;
    }

    public int getHeight() {
        return mask.length;
    }

    public boolean isMasked(int x, int y) {
        return mask[y][x];
    }
}