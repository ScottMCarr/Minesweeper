package sc.minesweeper.ui;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.layout.Region;

public class DigitDisplay extends Region {
    private static final String DEFAULT_STYLE_CLASS = "digit-display";
    private final ObjectProperty<Digit> digit = new SimpleObjectProperty<>(this, "digit");

    public DigitDisplay() {
        this.getStyleClass().add(DEFAULT_STYLE_CLASS);
        digit.addListener(new EnumStyleClassListener<>(this));
        this.setDigit(Digit.ZERO);
    }

    public Digit getDigit() {
        return digit.get();
    }

    public void setDigit(Digit digit) {
        this.digit.set(digit);
    }

    public ObjectProperty<Digit> digitProperty() {
        return digit;
    }

}
