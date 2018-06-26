package sc.minesweeper.ui;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

public class BorderedPane extends AnchorPane {

    private final BorderPane root = new BorderPane(null, new BorderPane(), null, new BorderPane(), null);
    private final ObjectProperty<Node> top = new SimpleObjectProperty<Node>(this, "top") {
        @Override
        protected void invalidated() {
            ((BorderPane) root.getTop()).setCenter(this.get());
        }
    };
    private final ObjectProperty<Node> left = new SimpleObjectProperty<Node>(this, "left") {
        @Override
        protected void invalidated() {
            root.setLeft(this.get());
        }
    };
    private final ObjectProperty<Node> right = new SimpleObjectProperty<Node>(this, "right") {
        @Override
        protected void invalidated() {
            root.setRight(this.get());
        }
    };
    private final ObjectProperty<Node> bottom = new SimpleObjectProperty<Node>(this, "bottom") {
        @Override
        protected void invalidated() {
            ((BorderPane) root.getBottom()).setCenter(this.get());
        }
    };
    private final ObjectProperty<Node> topLeft = new SimpleObjectProperty<Node>(this, "topLeft") {
        @Override
        protected void invalidated() {
            ((BorderPane) root.getTop()).setLeft(this.get());
        }
    };
    private final ObjectProperty<Node> topRight = new SimpleObjectProperty<Node>(this, "topRight") {
        @Override
        protected void invalidated() {
            ((BorderPane) root.getTop()).setRight(this.get());
        }
    };
    private final ObjectProperty<Node> bottomLeft = new SimpleObjectProperty<Node>(this, "bottomLeft") {
        @Override
        protected void invalidated() {
            ((BorderPane) root.getBottom()).setLeft(this.get());
        }
    };
    private final ObjectProperty<Node> bottomRight = new SimpleObjectProperty<Node>(this, "bottomRight") {
        @Override
        protected void invalidated() {
            ((BorderPane) root.getBottom()).setRight(this.get());
        }
    };

    public BorderedPane() {
        this(null);
    }

    public BorderedPane(Node view) {
        this.getChildren().add(root);
        this.setView(view);
    }

    public BorderedPane(Node view, Node top, Node left, Node right, Node bottom, Node topLeft, Node topRight, Node bottomLeft, Node bottomRight) {
        this.getChildren().add(root);
        this.setView(view);
        this.setTop(top);
        this.setLeft(left);
        this.setRight(right);
        this.setBottom(bottom);
        this.setTopLeft(topLeft);
        this.setTopRight(topRight);
        this.setBottomLeft(bottomLeft);
        this.setBottomRight(bottomRight);
        AnchorPane.setTopAnchor(root, 0.0);
        AnchorPane.setRightAnchor(root, 0.0);
        AnchorPane.setLeftAnchor(root, 0.0);
        AnchorPane.setBottomAnchor(root, 0.0);
    }

    public Node getView() {
        return root.getCenter();
    }

    public void setView(Node node) {
        root.setCenter(node);
    }

    public Node getTop() {
        return top.get();
    }

    public void setTop(Node top) {
        this.top.set(top);
    }

    public ObjectProperty<Node> topProperty() {
        return top;
    }

    public Node getLeft() {
        return left.get();
    }

    public void setLeft(Node left) {
        this.left.set(left);
    }

    public ObjectProperty<Node> leftProperty() {
        return left;
    }

    public Node getRight() {
        return right.get();
    }

    public void setRight(Node right) {
        this.right.set(right);
    }

    public ObjectProperty<Node> rightProperty() {
        return right;
    }

    public Node getBottom() {
        return bottom.get();
    }

    public void setBottom(Node bottom) {
        this.bottom.set(bottom);
    }

    public ObjectProperty<Node> bottomProperty() {
        return bottom;
    }

    public Node getTopLeft() {
        return topLeft.get();
    }

    public void setTopLeft(Node topLeft) {
        this.topLeft.set(topLeft);
    }

    public ObjectProperty<Node> topLeftProperty() {
        return topLeft;
    }

    public Node getTopRight() {
        return topRight.get();
    }

    public void setTopRight(Node topRight) {
        this.topRight.set(topRight);
    }

    public ObjectProperty<Node> topRightProperty() {
        return topRight;
    }

    public Node getBottomLeft() {
        return bottomLeft.get();
    }

    public void setBottomLeft(Node bottomLeft) {
        this.bottomLeft.set(bottomLeft);
    }

    public ObjectProperty<Node> bottomLeftProperty() {
        return bottomLeft;
    }

    public Node getBottomRight() {
        return bottomRight.get();
    }

    public void setBottomRight(Node bottomRight) {
        this.bottomRight.set(bottomRight);
    }

    public ObjectProperty<Node> bottomRightProperty() {
        return bottomRight;
    }
}
