package sc.minesweeper.ui;

import com.sun.javafx.css.StyleManager;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.css.*;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class TiledImageView extends Canvas {
    private static final String DEFAULT_STYLE_CLASS = "tiled-image-view";
    private final ObjectProperty<Image> image = new SimpleObjectProperty<>(this, "image");
    private ObjectProperty<Rectangle2D> viewport;
    private StyleableStringProperty imageUrl;
    private StyleableObjectProperty<Insets> viewportInsets;
    public TiledImageView() {
        this(null);
    }
    public TiledImageView(Image image) {
        this.getStyleClass().add(DEFAULT_STYLE_CLASS);
        this.setImage(image);
    }

    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
        return StyleableProperties.STYLEABLES;
    }

    private StyleableStringProperty imageUrlProperty() {
        if (imageUrl == null) {
            imageUrl = new SimpleStyleableStringProperty(StyleableProperties.IMAGE, this, "imageUrl") {
                @Override
                protected void invalidated() {
                    String imageUrl = this.get();
                    if (imageUrl != null) {
                        setImage(StyleManager.getInstance().getCachedImage(imageUrl));
                    } else {
                        setImage(null);
                    }
                }
            };
        }
        return imageUrl;
    }

    private StyleableObjectProperty<Insets> viewportInsetsProperty() {
        if (viewportInsets == null) {
            viewportInsets = new SimpleStyleableObjectProperty<Insets>(StyleableProperties.VIEWPORT, this, "viewportInsets") {
                @Override
                protected void invalidated() {
                    Insets insets = this.get();
                    if (insets != null) {
                        setViewport(new Rectangle2D(insets.getTop(), insets.getRight(), insets.getBottom(), insets.getLeft()));
                    } else {
                        setViewport(null);
                    }
                }
            };
        }
        return viewportInsets;
    }

    public Image getImage() {
        return image.get();
    }

    public void setImage(Image image) {
        this.image.set(image);
    }

    public ObjectProperty<Image> imageProperty() {
        return image;
    }

    public Rectangle2D getViewport() {
        return viewport.get();
    }

    public void setViewport(Rectangle2D viewport) {
        this.viewportProperty().set(viewport);
    }

    public ObjectProperty<Rectangle2D> viewportProperty() {
        if (viewport == null) {
            this.viewport = new SimpleObjectProperty<Rectangle2D>(this, "viewport") {
                @Override
                protected void invalidated() {
                    redraw();
                }
            };
        }
        return viewport;
    }

    private void redraw() {
        GraphicsContext graphics = this.getGraphicsContext2D();
        Image image = this.getImage();
        double width = this.getWidth();
        double height = this.getHeight();
        if (image == null) {
            graphics.clearRect(0, 0, width, height);
        } else {
            Rectangle2D viewport = this.getViewport();
            if (viewport == null) {
                viewport = new Rectangle2D(0, 0, image.getWidth(), image.getHeight());
            }
            for (int x = 0; x < width; x += viewport.getWidth()) {
                double clipWidth = Math.min(viewport.getWidth(), width - x);
                for (int y = 0; y < height; y += viewport.getHeight()) {
                    double clipHeight = Math.min(viewport.getHeight(), height - y);
                    graphics.drawImage(
                            image,
                            viewport.getMinX(), viewport.getMinY(), clipWidth, clipHeight,
                            x, y, clipWidth, clipHeight
                    );
                }
            }
        }
    }

    @Override
    public boolean isResizable() {
        return true;
    }

    @Override
    public double minWidth(double height) {
        return 0;
    }

    @Override
    public double minHeight(double width) {
        return 0;
    }

    @Override
    public double prefWidth(double height) {
        Image image = this.getImage();
        if (image != null) {
            Rectangle2D viewport = this.getViewport();
            if (viewport != null) {
                return viewport.getWidth();
            }
            return image.getWidth();
        }
        return super.prefWidth(height);
    }

    @Override
    public double prefHeight(double width) {
        Image image = this.getImage();
        if (image != null) {
            Rectangle2D viewport = this.getViewport();
            if (viewport != null) {
                return viewport.getHeight();
            }
            return image.getHeight();
        }
        return super.prefHeight(width);
    }

    @Override
    public double maxWidth(double height) {
        return Double.MAX_VALUE;
    }

    @Override
    public double maxHeight(double width) {
        return Double.MAX_VALUE;
    }

    @Override
    public void resize(double width, double height) {
        super.setWidth(width);
        super.setHeight(height);
        this.redraw();
    }

    @Override
    public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
        return getClassCssMetaData();
    }

    private static class StyleableProperties {
        private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

        private static final CssMetaData<TiledImageView, String> IMAGE = new CssMetaData<TiledImageView, String>(
                "-fx-image",
                StyleConverter.getUrlConverter(),
                null,
                false,
                new ArrayList<>()
        ) {
            @Override
            public boolean isSettable(TiledImageView n) {
                return !n.imageProperty().isBound();
            }

            @Override
            public StyleableProperty<String> getStyleableProperty(TiledImageView n) {
                return n.imageUrlProperty();
            }
        };

        private static final CssMetaData<TiledImageView, Insets> VIEWPORT = new CssMetaData<TiledImageView, Insets>(
                "-fx-viewport",
                StyleConverter.getInsetsConverter(),
                null,
                false,
                new ArrayList<>()
        ) {
            @Override
            public boolean isSettable(TiledImageView n) {
                return !n.viewportProperty().isBound();
            }

            @Override
            public StyleableProperty<Insets> getStyleableProperty(TiledImageView n) {
                return n.viewportInsetsProperty();
            }
        };

        static {
            List<CssMetaData<? extends Styleable, ?>> styleables = new ArrayList<>(Canvas.getClassCssMetaData());
            styleables.add(IMAGE);
            styleables.add(VIEWPORT);
            STYLEABLES = Collections.unmodifiableList(styleables);
        }
    }
}
