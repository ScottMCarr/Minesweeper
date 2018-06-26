package sc.minesweeper.ui;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;
import sc.minesweeper.Board;

import java.util.concurrent.TimeUnit;

public class StatusBar extends BorderPane {
    private static final String DEFAULT_STYLE_CLASS = "status-bar";

    private final ObjectProperty<State> state = new SimpleObjectProperty<>(this, "state");
    private final Timeline timeline;
    private final Timer timer = new Timer();
    private final NumberDisplay timeDisplay = new NumberDisplay(3);

    public StatusBar(GameBoard board) {
        this.getStyleClass().add(DEFAULT_STYLE_CLASS);
        GameButton stateButton = new GameButton();
        stateButton.setOnAction(event -> board.setBoard(Board.generate(board.getBoard())));
        state.addListener(new EnumStyleClassListener<>(stateButton));
        state.bind(Bindings.createObjectBinding(() -> State.from(board.getState()), board.stateProperty()));
        this.setCenter(stateButton);
        NumberDisplay bombsLeft = new NumberDisplay(3);
        bombsLeft.valueProperty().bind(Bindings.createIntegerBinding(() -> {
            Board b = board.getBoard();
            return b == null ? 0 : b.getMines() - board.placedFlagsProperty().get();
        }, board.placedFlagsProperty()));
        this.setLeft(bombsLeft);
        this.setRight(timeDisplay);
        this.timeline = new Timeline(new KeyFrame(Duration.millis(900), event -> {
            long seconds = timer.getRuntime(TimeUnit.SECONDS);
            if (seconds < timeDisplay.getMax()) {
                timeDisplay.setValue((int) seconds);
            }
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        board.startedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                timer.start();
                timeline.play();
            }
        });
        board.stateProperty().addListener((observable, oldValue, newValue) -> this.updateGameState(newValue));
    }

    private void updateGameState(GameBoard.State state) {
        switch (state) {
            case INITALIZED: {
                timer.stop();
                timeDisplay.setValue(0);
            }
            case LOST:
            case WON: {
                timeline.stop();
                break;
            }
        }
    }

    private enum State {
        DEFAULT,
        GUESSING,
        LOST,
        WON;

        private static State from(GameBoard.State state) {
            switch (state) {
                case INITALIZED:
                case IDLING: {
                    return DEFAULT;
                }
                case GUESSING: {
                    return GUESSING;
                }
                case WON: {
                    return WON;
                }
                case LOST: {
                    return LOST;
                }
            }
            throw new AssertionError();
        }
    }

    private static class Timer {
        private long started = -1;

        public boolean isRunning() {
            return started != -1;
        }

        public void start() {
            this.started = System.currentTimeMillis();
        }

        public void stop() {
            this.started = -1;
        }

        public long getRuntime(TimeUnit unit) {
            return unit.convert(System.currentTimeMillis() - started, TimeUnit.MILLISECONDS);
        }
    }
}
