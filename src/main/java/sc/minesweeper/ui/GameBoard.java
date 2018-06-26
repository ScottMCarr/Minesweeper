package sc.minesweeper.ui;

import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import sc.minesweeper.Board;

import java.util.*;

public class GameBoard extends GridPane {
    private static final String DEFAULT_STYLE_CLASS = "game-board";

    private final ReadOnlyObjectWrapper<State> state = new ReadOnlyObjectWrapper<>(this, "state");
    private int width, height;
    private GameTile[] tiles;
    private final ObjectProperty<Board> board = new SimpleObjectProperty<Board>(this, "board") {
        @Override
        protected void invalidated() {
            Board board = this.get();
            if (board != null) {
                GameBoard.this.resize(board.getWidth(), board.getHeight());
            } else {
                GameBoard.this.resize(0, 0);
            }
            GameBoard.this.setupPlay();
        }
    };
    private final ReadOnlyIntegerWrapper placedFlags = new ReadOnlyIntegerWrapper(this, "placedFlags");
    private final ChangeListener<GameTile.State> flagListener = (observable, oldValue, newValue) -> {
        int newCount = placedFlags.get();
        if (oldValue == GameTile.State.FLAGGED) {
            newCount--;
        } else if (newValue == GameTile.State.FLAGGED) {
            newCount++;
        }
        placedFlags.set(Math.max(0, newCount));
    };
    private final BooleanProperty started = new SimpleBooleanProperty(this, "started");

    public GameBoard(Board board) {
        this.getStyleClass().add(DEFAULT_STYLE_CLASS);
        this.setBoard(board);
    }

    public Board getBoard() {
        return board.get();
    }

    public void setBoard(Board board) {
        this.board.set(board);
    }

    public ObjectProperty<Board> boardProperty() {
        return board;
    }

    public State getState() {
        return state.get();
    }

    public ReadOnlyObjectProperty<State> stateProperty() {
        return state.getReadOnlyProperty();
    }

    public int getPlacedFlags() {
        return placedFlags.get();
    }

    public ReadOnlyIntegerProperty placedFlagsProperty() {
        return placedFlags.getReadOnlyProperty();
    }

    public List<GameTile> getTiles() {
        return Arrays.asList(tiles);
    }

    public boolean isStarted() {
        return started.get();
    }

    public BooleanProperty startedProperty() {
        return started;
    }

    public GameTile getTileAt(int x, int y) {
        if (x < 0 || y < 0 || x >= width || y >= height) {
            throw new IllegalArgumentException();
        }
        return tiles[x + (y * width)];
    }

    private void setupPlay() {
        state.set(State.INITALIZED);
        started.set(false);
        placedFlags.set(0);
        Board board = this.board.get();
        if (board == null) {
            return;
        }
        for (Node child : this.getChildren()) {
            ((GameTile) child).setValue(board.get(GridPane.getColumnIndex(child), GridPane.getRowIndex(child)));
        }
        for (GameTile tile : tiles) {
            tile.setOpened(false);
            tile.setState(GameTile.State.BLANK);
            tile.setOnAction(event -> {
                if (!tile.isFlagged() && !tile.isOpened()) {
                    started.set(true);
                    Set<GameTile> tiles = Collections.newSetFromMap(new IdentityHashMap<>());
                    GameBoard.this.findEdges(tile, tiles);
                    for (GameTile t : tiles) {
                        t.setOpened(true);
                    }
                    GameBoard.this.checkCompletion();
                }
            });
            tile.setOnMousePressed(event -> {
                if (event.getButton() == MouseButton.PRIMARY) {
                    state.set(State.GUESSING);
                } else if (event.getButton() == MouseButton.SECONDARY) {
                    if (!tile.isOpened()) {
                        tile.setState(getNext(tile.getState()));
                    }
                }
            });
            tile.setOnMouseReleased(event -> {
                state.set(State.IDLING);
            });
        }
    }

    private GameTile.State getNext(GameTile.State state) {
        switch (state) {
            case BLANK: {
                return GameTile.State.FLAGGED;
            }
            case FLAGGED: {
                return GameTile.State.UNKNOWN;//todo check if settings allow unknown
            }
            case UNKNOWN: {
                return GameTile.State.BLANK;
            }
        }
        throw new AssertionError();
    }

    private void clearHandlers(GameTile tile) {
        tile.setOnAction(null);
        tile.setOnMousePressed(null);
        tile.setOnMouseReleased(null);
    }

    public Collection<GameTile> getSurroundingTiles(GameTile tile) {
        int x = GridPane.getColumnIndex(tile);
        int y = GridPane.getRowIndex(tile);
        List<GameTile> tiles = new ArrayList<>(8);
        for (int xOff = -1; xOff <= 1; xOff++) {
            for (int yOff = -1; yOff <= 1; yOff++) {
                if (xOff == 0 && yOff == 0) {
                    continue;
                }
                int relX = x + xOff;
                int relY = y + yOff;
                if (relX >= 0 && relY >= 0 && relX < width && relY < height) {
                    tiles.add(this.tiles[relX + (relY * width)]);
                }
            }
        }
        return tiles;
    }

    private void findEdges(GameTile tile, Set<GameTile> traversed) {
        if (traversed.add(tile) && tile.getValue() == Board.Value.EMPTY) {
            for (GameTile t : this.getSurroundingTiles(tile)) {
                if (!t.isFlagged() && !t.isOpened()) {
                    this.findEdges(t, traversed);
                }
            }
        }
    }

    private void checkCompletion() {
        for (GameTile tile : tiles) {
            if (tile.isOpened() && tile.getValue() == Board.Value.MINE) {
                this.handleLose(tile);
                return;
            }
        }
        for (GameTile tile : tiles) {
            if (!tile.isOpened() && tile.getValue() != Board.Value.MINE) {
                return;
            }
        }
        this.handleWin();
    }

    private void handleWin() {
        this.state.set(State.WON);
        for (GameTile tile : tiles) {
            this.clearHandlers(tile);
            if (tile.getValue() == Board.Value.MINE) {
                tile.setState(GameTile.State.FLAGGED);
            }
        }
    }

    private void handleLose(GameTile mine) {
        mine.setState(GameTile.State.INCORRECT);
        this.state.set(State.LOST);
        for (GameTile tile : tiles) {
            this.clearHandlers(tile);
            if (tile.getValue() == Board.Value.MINE) {
                if (!tile.isOpened() && !tile.isFlagged()) {
                    tile.setOpened(true);
                }
            } else {
                if (tile.isFlagged()) {
                    tile.setState(GameTile.State.INCORRECT);
                }
            }
        }
    }

    private void resize(int width, int height) {
        if (this.width == width && this.height == height) {
            return;
        }
        if (this.width > width || this.height > height) {
            this.getChildren().removeIf(child -> {
                if (GridPane.getColumnIndex(child) >= width || GridPane.getRowIndex(child) >= height) {
                    ((GameTile) child).stateProperty().removeListener(flagListener);
                    return true;
                }
                return false;
            });
        }
        if (this.width < width || this.height < height) {
            for (int x = this.width; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    this.createTile(x, y);
                }
            }
            for (int y = this.height; y < height; y++) {
                for (int x = this.width - 1; x >= 0; x--) {
                    this.createTile(x, y);
                }
            }
        }
        GameTile[] tiles = new GameTile[width * height];
        for (Node child : this.getChildren()) {
            tiles[GridPane.getColumnIndex(child) + (GridPane.getRowIndex(child) * width)] = (GameTile) child;
        }
        this.width = width;
        this.height = height;
        this.tiles = tiles;
    }

    private void createTile(int x, int y) {
        GameTile tile = new GameTile();
        tile.stateProperty().addListener(flagListener);
        this.add(tile, x, y);
    }

    public enum State {
        INITALIZED,
        IDLING,
        GUESSING,
        WON,
        LOST
    }
}
