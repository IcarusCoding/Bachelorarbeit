package de.intelligence.bachelorarbeit.demoapplications;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public final class TestComponent extends VBox {

    final StringProperty numberOne;
    final StringProperty numberTwo;

    public TestComponent() {
        final Label oneLabel = new Label();
        this.numberOne = new SimpleStringProperty();
        oneLabel.textProperty().bind(this.numberOne);
        final Label twoLabel = new Label();
        this.numberTwo = new SimpleStringProperty();
        twoLabel.textProperty().bind(this.numberTwo);
        this.getChildren().addAll(oneLabel, twoLabel);
        this.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
    }

    public String getNumberOne() {
        return numberOne.get();
    }

    public void setNumberOne(String numberOne) {
        this.numberOne.set(numberOne);
    }

    public StringProperty numberOneProperty() {
        return numberOne;
    }

    public String getNumberTwo() {
        return numberTwo.get();
    }

    public void setNumberTwo(String numberTwo) {
        this.numberTwo.set(numberTwo);
    }

    public StringProperty numberTwoProperty() {
        return numberTwo;
    }

}