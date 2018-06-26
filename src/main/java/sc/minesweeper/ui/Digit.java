package sc.minesweeper.ui;

enum Digit {
    NEGATIVE,
    ZERO,
    ONE,
    TWO,
    THREE,
    FOUR,
    FIVE,
    SIX,
    SEVEN,
    EIGHT,
    NINE;

    private static final Digit[] values = Digit.values();

    public static Digit from(int v) {
        if (v < 0 || v >= 10) {
            throw new IllegalArgumentException();
        }
        return values[v + 1];
    }

    public static Digit from(char c) {
        if (c == '-') {
            return NEGATIVE;
        }
        if (c < '0' || c > '9') {
            throw new IllegalArgumentException();
        }
        return values[(c - '0') + 1];
    }
}
