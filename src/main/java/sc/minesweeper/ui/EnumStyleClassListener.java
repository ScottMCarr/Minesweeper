package sc.minesweeper.ui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.Node;


public class EnumStyleClassListener<E extends Enum<E>> implements ChangeListener<E> {
    private final Node node;

    public EnumStyleClassListener(Node node) {
        this.node = node;
    }

    private static String format(Enum e) {
        char[] nameChars = e.name().toCharArray();
        for (int i = nameChars.length - 1; i >= 0; i--) {
            char c = nameChars[i];
            if (c == '_') {
                c = '-';
            } else if (Character.isUpperCase(c)) {
                c = Character.toLowerCase(c);
            }
            nameChars[i] = c;
        }
        return new String(nameChars);
    }

    @Override
    public void changed(ObservableValue<? extends E> observable, E oldValue, E newValue) {
        ObservableList<String> classes = node.getStyleClass();
        if (oldValue != null) {
            classes.remove(format(oldValue));
        }
        if (newValue != null) {
            classes.add(format(newValue));
        }
    }
}
