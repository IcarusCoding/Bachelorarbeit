package de.intelligence.bachelorarbeit.simplifx.controller;

import java.util.function.Consumer;

import javafx.animation.Timeline;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

public class ControllerGroupWrapperImpl implements IControllerGroupWrapper {

    private final ReadOnlyObjectWrapper<Pane> wrapper;

    public ControllerGroupWrapperImpl() {
        this.wrapper = new ReadOnlyObjectWrapper<>(new StackPane());
    }

    private void switchController0(IController handler, Timeline prev, Timeline next, Consumer<SwitchState> stateConsumer) {
        stateConsumer.accept(SwitchState.SWITCH_STARTED);
        this.wrapper.get().prefHeightProperty().bind(handler.getRoot().prefHeightProperty());
        this.wrapper.get().prefWidthProperty().bind(handler.getRoot().prefWidthProperty());
        //TODO do animation stuff
        // ....
        this.setController(handler.getRoot());
        stateConsumer.accept(SwitchState.SWITCH_ENDED);
    }

    private void setController(Pane pane) {
        this.wrapper.get().getChildren().setAll(pane);
    }

    @Override
    public void switchController(IController handler, IWrapperAnimationFactory animationFactory, Consumer<SwitchState> stateConsumer) {
        this.switchController0(handler, animationFactory.createForNext(handler.getRoot()),
                animationFactory.createForPrevious(wrapper.get()), stateConsumer);
    }

    @Override
    public void setController(IController handler) {
        if (this.wrapper.get() == null) { // first controller
            setController(handler.getRoot());
            return;
        }
        this.switchController(handler, new DefaultWrapperAnimationFactory(), state -> {
        });
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
