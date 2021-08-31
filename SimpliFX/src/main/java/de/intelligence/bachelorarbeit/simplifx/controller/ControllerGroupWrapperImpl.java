package de.intelligence.bachelorarbeit.simplifx.controller;

import java.util.Optional;
import java.util.function.Function;

import javafx.animation.Timeline;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
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
    private final ObjectProperty<INotificationDialog> dialog;

    private Timeline running;

    public ControllerGroupWrapperImpl(Function<Pane, INotificationDialog> dialog) {
        this.wrapper = new ReadOnlyObjectWrapper<>(new StackPane());
        this.dialog = new SimpleObjectProperty<>(dialog.apply(this.wrapper.get()));
        final Rectangle clipRect = new Rectangle();
        clipRect.widthProperty().bind(this.wrapper.get().widthProperty());
        clipRect.heightProperty().bind(this.wrapper.get().heightProperty());
        clipRect.xProperty().bind(this.wrapper.get().translateXProperty().multiply(-1));
        clipRect.yProperty().bind(this.wrapper.get().translateYProperty().multiply(-1));
        this.wrapper.get().setClip(clipRect);
    }

    private void switchController0(IController controller, IWrapperAnimation animationFactory) {
        this.wrapper.get().prefHeightProperty().bind(controller.getRoot().prefHeightProperty());
        this.wrapper.get().prefWidthProperty().bind(controller.getRoot().prefWidthProperty());
        if (this.wrapper.get().getChildren().isEmpty()) {
            this.setController(controller.getRoot());
        } else {
            if (this.running != null) {
                this.running.stop();
            }
            this.running = animationFactory.create(this.wrapper.get(), (Pane) this.wrapper.get().getChildren().get(0),
                    controller.getRoot());
            if (this.running == null) {
                this.setController(controller.getRoot());
            } else {
                this.running.play();
            }
        }
        this.getNextFocus(controller.getRoot()).ifPresentOrElse(Node::requestFocus, () -> {
            controller.getRoot().setFocusTraversable(true);
            controller.getRoot().requestFocus();
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
    public void switchController(IController controller, IWrapperAnimation animationFactory) {
        this.switchController0(controller, animationFactory);
    }

    @Override
    public void setController(IController controller) {
        if (this.wrapper.get().getChildren().isEmpty()) {
            this.setController(controller.getRoot());
            return;
        }
        this.switchController(controller, new DefaultWrapperAnimation());
    }

    @Override
    public Pane getWrapper() {
        return this.wrapper.get();
    }

    @Override
    public void showNotification(StringBinding title, StringBinding content, NotificationKind kind) {
        final INotificationDialog dialog = this.dialog.get();
        if (dialog != null) {
            dialog.handleMessage(title, content, kind);
        }
    }

    @Override
    public void destroy() {
        this.dialog.get().reset();
        final Pane w = this.wrapper.get();
        if (w != null && w.getParent() != null && w.getParent() instanceof Pane) {
            ((Pane) w.getParent()).getChildren().remove(w);
        }
        this.dialog.unbind();
        this.dialog.set(null);
        this.wrapper.unbind();
        this.wrapper.set(null);
    }

}
