package sc.minesweeper.ui;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;

public class NumberDisplay extends HBox {
    private static final String DEFAULT_STYLE_CLASS = "number-display";

    private final long min;
    private final long max;
    private final DigitDisplay[] digitDisplays;
    private final IntegerProperty value = new SimpleIntegerProperty(this, "value") {
        @Override
        protected void invalidated() {
            int val = this.get();
            int i = 0;
            if (val < 0) {
                digitDisplays[i++].setDigit(Digit.NEGATIVE);
                val = -val;
            }
            String s = String.valueOf(val);
            for (int j = digitDisplays.length - i - s.length(); j > 0; j--) {
                digitDisplays[i++].setDigit(Digit.ZERO);
            }
            for (int j = 0; j < s.length(); j++) {
                digitDisplays[i].setDigit(Digit.from(s.charAt(j)));
                i++;
            }
        }
    };

    public NumberDisplay(int digits) {
        if (digits <= 0) {
            throw new IllegalArgumentException();
        }
        this.min = (long) -Math.pow(10, digits - 1);
        this.max = (long) Math.pow(10, digits);
        this.digitDisplays = new DigitDisplay[digits];
        for (int i = 0; i < digits; i++) {
            digitDisplays[i] = new DigitDisplay();
        }
        this.getStyleClass().add(DEFAULT_STYLE_CLASS);
        this.getChildren().addAll(digitDisplays);
        this.setAlignment(Pos.CENTER);
    }

    public long getMin() {
        return min;
    }

    public long getMax() {
        return max;
    }

    public int getValue() {
        return value.get();
    }

    public void setValue(int value) {
        if (value <= min || max <= value) {
            throw new IllegalArgumentException();
        }
        this.value.set(value);
    }

    public IntegerProperty valueProperty() {
        return value;
    }
}
