package de.intelligence.bachelorarbeit.simplifx.controller;

import java.util.Optional;

import javafx.animation.Timeline;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.Node;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.Control;
import javafx.scene.control.TextInputControl;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

import de.intelligence.bachelorarbeit.simplifx.controller.animation.DefaultWrapperAnimation;
import de.intelligence.bachelorarbeit.simplifx.controller.animation.IWrapperAnimation;

public class ControllerGroupWrapperImpl implements IControllerGroupWrapper {

    private final ReadOnlyObjectWrapper<Pane> wrapper;
    private Timeline running;

    public ControllerGroupWrapperImpl() {
        this.wrapper = new ReadOnlyObjectWrapper<>(new StackPane());
        final Rectangle clipRect = new Rectangle();
        clipRect.widthProperty().bind(this.wrapper.get().widthProperty());
        clipRect.heightProperty().bind(this.wrapper.get().heightProperty());
        clipRect.xProperty().bind(this.wrapper.get().translateXProperty().multiply(-1));
        clipRect.yProperty().bind(this.wrapper.get().translateYProperty().multiply(-1));
        this.wrapper.get().setClip(clipRect);
    }

    private void switchController0(IController handler, IWrapperAnimation animationFactory) {
        this.wrapper.get().prefHeightProperty().bind(handler.getRoot().prefHeightProperty());
        this.wrapper.get().prefWidthProperty().bind(handler.getRoot().prefWidthProperty());
        if (this.wrapper.get().getChildren().isEmpty()) {
            this.setController(handler.getRoot());
            return;
        }
        if (this.running != null) {
            this.running.stop();
        }
        this.running = animationFactory.create(this.wrapper.get(), (Pane) this.wrapper.get().getChildren().get(0),
                handler.getRoot());
        if (this.running == null) {
            this.setController(handler.getRoot());
            return;
        }
        this.running.play();
        this.getNextFocus(handler.getRoot()).ifPresentOrElse(Node::requestFocus, () -> {
            handler.getRoot().setFocusTraversable(true);
            handler.getRoot().requestFocus();
        });
    }

    private void setController(Pane pane) {
        this.wrapper.get().getChildren().setAll(pane);
    }

    private <E extends Pane> Optional<Control> getNextFocus(E root) {
        for (Node n : root.getChildren()) {
            if (n instanceof Pane) {
                final Optional<Control> found = getNextFocus((Pane) n);
                if (found.isPresent()) {
                    return found;
                }
                continue;
            }
            if ((n instanceof ButtonBase || n instanceof TextInputControl) && n.isFocusTraversable()) {
                return Optional.of((Control) n);
            }
        }
        return Optional.empty();
    }

    @Override
    public void switchController(IController handler, IWrapperAnimation animationFactory) {
        this.switchController0(handler, animationFactory);
    }

    @Override
    public void setController(IController handler) {
        if (this.wrapper.get().getChildren().size() == 0) {
            this.setController(handler.getRoot());
            return;
        }
        this.switchController(handler, new DefaultWrapperAnimation());
    }

    @Override
    public ReadOnlyObjectProperty<Pane> wrapperProperty() {
        return this.wrapper.getReadOnlyProperty();
    }

    @Override
    public Pane getWrapper() {
        return this.wrapper.get();
    }

}
