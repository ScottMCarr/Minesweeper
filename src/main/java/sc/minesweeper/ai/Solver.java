package sc.minesweeper.ai;

import javafx.application.Platform;
import sc.minesweeper.Board;
import sc.minesweeper.ui.GameBoard;
import sc.minesweeper.ui.GameTile;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Solver implements Runnable {
    private final GameBoard board;
    private final int actionRate;
    private final Random random = new Random();
    private volatile boolean running;

    public Solver(GameBoard board, int actionRate) {
        this.board = board;
        this.actionRate = actionRate;
    }

    public void stop() {
        this.running = false;
    }

    public void run() {
        BlockingQueue<Runnable> actions = new LinkedBlockingQueue<>();
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if (board.getState() != GameBoard.State.WON && board.getState() != GameBoard.State.LOST) {
                    List<GameTile> tiles = board.getTiles();
                    if (tiles.stream().noneMatch(GameTile::isOpened)) {
                        tiles.get(random.nextInt(tiles.size())).fire();
                    }
                    for (GameTile tile : tiles) {
                        if (tile.isOpened()) {
                            if (tile.getValue() != Board.Value.EMPTY) {
                                Collection<GameTile> surrounding = board.getSurroundingTiles(tile);
                                long found = surrounding.stream().filter(GameTile::isFlagged).count();
                                if (found == tile.getValue().getIntValue()) {
                                    surrounding.stream().filter(t -> !t.isOpened() && !t.isFlagged()).forEach(t -> {
                                        actions.offer(t::fire);
                                    });
                                } else {
                                    long hidden = surrounding.stream().filter(t -> !t.isOpened()).count();
                                    if (hidden == tile.getValue().getIntValue()) {
                                        surrounding.stream().filter(t -> !t.isOpened() && !t.isFlagged()).forEach(t -> {
                                            actions.offer(() -> t.setState(GameTile.State.FLAGGED));
                                        });
                                    }
                                }
                            }
                        }
                    }
                    if (actions.isEmpty()) {
                        Map<GameTile, Double> weights = new HashMap<>();
                        for (GameTile tile : board.getTiles()) {
                            if (!tile.isOpened()) {

                            }
                        }
                    }
                }
                actions.offer(this);
            }
        });

        this.running = true;
        while (running) {
            try {
                Runnable runnable = actions.take();
                Platform.runLater(runnable);
                Thread.sleep(actionRate);
            } catch (InterruptedException e) {
                return;
            }
        }
    }
}
