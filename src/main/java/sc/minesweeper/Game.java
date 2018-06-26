package sc.minesweeper;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import sc.minesweeper.ai.Solver;
import sc.minesweeper.ui.BorderedPane;
import sc.minesweeper.ui.GameBoard;
import sc.minesweeper.ui.StatusBar;
import sc.minesweeper.ui.TiledImageView;

public class Game extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Board board = Board.generate(30, 16, 50);
        MenuBar menuBar = new MenuBar(new Menu("Game"));//todo actual settings
        GameBoard gameBoard = new GameBoard(board);
        BorderedPane gameBorder = new BorderedPane(gameBoard);
        gameBoard.boardProperty().addListener(observable -> {
            primaryStage.sizeToScene();
        });
        Scene scene = new Scene(new BorderPane(
                new BorderPane(
                        borderedPane(gameBorder, false),
                        borderedPane(new StatusBar(gameBoard), true),
                        null,
                        null,
                        null
                ),
                menuBar,
                null,
                null,
                null
        ));

        scene.getStylesheets().add(Game.class.getClassLoader().getResource("minesweeper.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.setTitle("Minesweeper");
        primaryStage.sizeToScene();
        primaryStage.show();
        Thread thread = new Thread(new Solver(gameBoard, 100));
        thread.setDaemon(true);
        thread.start();
    }

    private BorderedPane borderedPane(Region view, boolean top) {
        BorderedPane pane = new BorderedPane(
                view,
                top ? this.imageView("top") : null,
                this.imageView("left"),
                this.imageView("right"),
                this.imageView("bottom"),
                top ? this.imageView("top-left") : null,
                top ? this.imageView("top-right") : null,
                this.imageView(top ? "joint-left" : "bottom-left"),
                this.imageView(top ? "joint-right" : "bottom-right")
        );
        if (top) {
            view.setBorder(new Border(new BorderStroke(Color.TRANSPARENT, null, null, new BorderWidths(0, 5, 0, 5))));
        }
        return pane;
    }

    private TiledImageView imageView(String className) {
        TiledImageView view = new TiledImageView();
        view.getStyleClass().addAll("game-border", className);
        return view;
    }
}
