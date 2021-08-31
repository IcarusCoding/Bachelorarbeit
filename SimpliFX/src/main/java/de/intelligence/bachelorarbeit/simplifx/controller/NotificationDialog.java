package de.intelligence.bachelorarbeit.simplifx.controller;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

import de.intelligence.bachelorarbeit.simplifx.utils.Conditions;

/**
 * A default {@link INotificationDialog} implementation that will be used if no other was specified.
 */
public final class NotificationDialog extends StackPane implements INotificationDialog {

    private static final double PREF_WIDTH = 300;
    private static final double PREF_HEIGHT = 60;

    private final ReadOnlyBooleanWrapper showing;
    private final AnchorPane root;
    private final Label titleLbl;
    private final Text contentText;
    private final Timeline animation;

    private String lastTitle;
    private int sameCount;

    public NotificationDialog(Pane parent) {
        Conditions.checkNull(parent, "Parent must not be null.");
        this.showing = new ReadOnlyBooleanWrapper();
        this.root = new AnchorPane();
        this.root.setMaxHeight(Double.NEGATIVE_INFINITY);
        this.root.setMaxWidth(Double.NEGATIVE_INFINITY);
        final HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.setSpacing(10);
        final VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER_LEFT);
        vbox.setPadding(new Insets(5, 0, 5, 0));
        this.titleLbl = new Label();
        this.titleLbl.setTextFill(Color.WHITE);
        this.titleLbl.setFont(Font.font(16));
        this.contentText = new Text();
        this.contentText.setFill(Color.WHITE);
        this.contentText.setFont(Font.font(13));
        vbox.getChildren().addAll(this.titleLbl, this.contentText);
        hbox.getChildren().add(vbox);
        this.root.getChildren().add(hbox);
        super.getChildren().add(this.root);
        StackPane.setAlignment(this.root, Pos.BOTTOM_CENTER);
        AnchorPane.setBottomAnchor(hbox, 5D);
        AnchorPane.setTopAnchor(hbox, 5D);
        AnchorPane.setLeftAnchor(hbox, 10D);
        AnchorPane.setRightAnchor(hbox, 10D);
        super.setPickOnBounds(false);
        super.maxWidthProperty().bind(parent.widthProperty());
        super.maxHeightProperty().bind(parent.heightProperty());
        this.root.prefWidthProperty().bind(Bindings.createDoubleBinding(() ->
                Math.min(NotificationDialog.PREF_WIDTH, parent.getWidth()), parent.widthProperty()));
        this.root.prefHeightProperty().bind(Bindings.createDoubleBinding(() ->
                Math.min(NotificationDialog.PREF_HEIGHT, parent.getHeight()), parent.heightProperty()));
        this.contentText.wrappingWidthProperty().bind(this.root.widthProperty().subtract(20));
        this.showing.addListener((obsVal, oldVal, newVal) -> {
            if (newVal && !parent.getChildren().contains(this)) {
                parent.getChildren().add(this);
            }
        });
        final DoubleProperty speedFactor = new SimpleDoubleProperty();
        final DoubleProperty testProp = new SimpleDoubleProperty();
        this.root.translateYProperty().bind(speedFactor.multiply(this.root.heightProperty()));
        this.animation = new Timeline(new KeyFrame(Duration.ZERO, new KeyValue(testProp, 1)),
                new KeyFrame(Duration.millis(3000), new KeyValue(testProp, 0)));
        this.animation.setOnFinished(e -> this.showing.set(false));
        final Timeline translationTimeline = new Timeline(new KeyFrame(Duration.ZERO, new KeyValue(speedFactor, 1)),
                new KeyFrame(Duration.millis(250), new KeyValue(speedFactor, 0, Interpolator.SPLINE(0.2, 0.7, 1, 1))));
        translationTimeline.setOnFinished(e -> {
            if (!this.showing.get()) {
                parent.getChildren().remove(this);
                this.lastTitle = null;
                this.sameCount = 0;
            }
        });
        this.showing.addListener((obsVal, oldVal, newVal) -> {
            translationTimeline.setRate(newVal ? 1 : -1);
            translationTimeline.play();
        });
    }

    @Override
    public void handleMessage(StringBinding title, StringBinding content, NotificationKind kind) {
        this.sameCount = ((title.get().equals(this.lastTitle) && content.get().equals(this.contentText.getText()) && (kind == this.root.getUserData())) ? (this.sameCount + 1) : 0);
        this.lastTitle = title.get();
        this.titleLbl.textProperty().bind(Bindings.createStringBinding(() ->
                this.sameCount > 0 ? String.format("%s [%d]", this.lastTitle, this.sameCount + 1) : this.lastTitle, title));
        this.contentText.textProperty().bind(content);
        this.root.setUserData(kind);
        this.root.setStyle("-fx-background-color: " + kind.getColor());
        this.animation.playFromStart();
        this.showing.set(true);
    }

    @Override
    public void reset() {
        this.showing.set(false);
    }

}
