package sc.minesweeper;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Board {
    private final int width;
    private final int height;
    private final int mines;
    private final boolean[] mineMap;

    private Board(int width, int height, int mines, boolean[] mineMap) {
        this.width = width;
        this.height = height;
        this.mines = mines;
        this.mineMap = mineMap;
    }

    public static Board generate(Board board) {
        return generate(board.getWidth(), board.getHeight(), board.getMines());
    }

    public static Board generate(int width, int height, int mines, long seed) {
        return generate(width, height, mines, new Random(seed));
    }

    public static Board generate(int width, int height, int mines) {
        return generate(width, height, mines, ThreadLocalRandom.current());
    }

    public static Board generate(int width, int height, int mines, Random random) {
        int tiles = width * height;
        if (tiles <= 0) {
            throw new IllegalArgumentException("Board has no tiles.");
        }
        if (mines <= 0) {
            throw new IllegalArgumentException("Board has no mines.");
        }
        if (mines >= tiles) {
            throw new IllegalArgumentException("Too many mines for the size of board.");
        }
        boolean[] mineMap = new boolean[tiles];
        for (int i = 0; i < mines; i++) {
            int index = random.nextInt(tiles);
            if (mineMap[index]) {
                int left = index - 1;
                int right = index + 1;
                while (true) {
                    if (left >= 0 && !mineMap[left]) {
                        index = left;
                        break;
                    }
                    if (right < tiles && !mineMap[right]) {
                        index = right;
                        break;
                    }
                    left--;
                    right++;
                }
            }
            mineMap[index] = true;
        }
        return new Board(width, height, mines, mineMap);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getMines() {
        return mines;
    }

    public Value get(int x, int y) {
        if (!this.withinRange(x, y)) {
            throw new IndexOutOfBoundsException();
        }
        if (this.mineAt(x, y)) {
            return Value.MINE;
        }
        int count = 0;
        for (int xOff = -1; xOff <= 1; xOff++) {
            for (int yOff = -1; yOff <= 1; yOff++) {
                if (this.mineAt(x + xOff, y + yOff)) {
                    count++;
                }
            }
        }
        return Value.COUNT_TO_VALUE[count];
    }

    private boolean withinRange(int x, int y) {
        return x >= 0 && y >= 0 && x < width && y < height;
    }

    private boolean mineAt(int x, int y) {
        return this.withinRange(x, y) && mineMap[x + (y * width)];
    }

    public enum Value {
        EMPTY(0),
        ONE(1),
        TWO(2),
        THREE(3),
        FOUR(4),
        FIVE(5),
        SIX(6),
        SEVEN(7),
        EIGHT(8),
        MINE(-1);

        private static final Value[] COUNT_TO_VALUE = new Value[]{
                EMPTY, ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT
        };

        private final int intValue;

        Value(int intValue) {
            this.intValue = intValue;
        }

        public int getIntValue() {
            return intValue;
        }
    }
}
