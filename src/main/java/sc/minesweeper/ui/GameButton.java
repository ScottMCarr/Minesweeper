package sc.minesweeper.ui;

import com.sun.javafx.scene.control.behavior.ButtonBehavior;
import com.sun.javafx.scene.control.skin.BehaviorSkinBase;
import javafx.event.ActionEvent;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.Skin;

public class GameButton extends ButtonBase {
    private static final String DEFAULT_STYLE_CLASS = "game-button";

    public GameButton() {
        this.getStyleClass().add(DEFAULT_STYLE_CLASS);
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new BehaviorSkinBase<GameButton, ButtonBehavior<GameButton>>(this, new ButtonBehavior<>(this)) {
        };
    }

    @Override
    public void fire() {
        this.fireEvent(new ActionEvent());
    }
}
