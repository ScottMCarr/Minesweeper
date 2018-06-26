package sc.minesweeper.ui;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import sc.minesweeper.Board;

public class GameTile extends GameButton {
    private static final String DEFAULT_STYLE_CLASS = "game-tile";
    private final ObjectProperty<Board.Value> value = new SimpleObjectProperty<>(this, "value");
    private final BooleanProperty opened = new SimpleBooleanProperty(this, "opened");
    private final ObjectProperty<State> state = new SimpleObjectProperty<>(this, "state", State.BLANK);

    public GameTile() {
        this.getStyleClass().add(DEFAULT_STYLE_CLASS);
        value.addListener((observable, oldValue, newValue) -> {
            if (oldValue != null) {
                this.getStyleClass().remove(oldValue.name().toLowerCase());
            }
            if (opened.get()) {
                this.getStyleClass().add(newValue.name().toLowerCase());
            }
        });
        state.addListener(new EnumStyleClassListener<>(this));
        opened.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                this.getStyleClass().add(value.get().name().toLowerCase());
            } else {
                this.getStyleClass().remove(value.get().name().toLowerCase());
            }
        });
    }

    public Board.Value getValue() {
        return value.get();
    }

    public void setValue(Board.Value value) {
        this.value.set(value);
    }

    public ObjectProperty<Board.Value> valueProperty() {
        return value;
    }

    public boolean isOpened() {
        return opened.get();
    }

    public void setOpened(boolean opened) {
        this.opened.set(opened);
    }

    public BooleanProperty openedProperty() {
        return opened;
    }

    public State getState() {
        return state.get();
    }

    public void setState(State state) {
        this.state.set(state);
    }

    public ObjectProperty<State> stateProperty() {
        return state;
    }

    public boolean isFlagged() {
        return state.get() == State.FLAGGED;
    }

    public enum State {
        BLANK,
        FLAGGED,
        UNKNOWN,
        INCORRECT
    }
}
