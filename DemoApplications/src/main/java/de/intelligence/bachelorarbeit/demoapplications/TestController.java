package de.intelligence.bachelorarbeit.demoapplications;

import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.chart.AreaChart;

import de.intelligence.bachelorarbeit.simplifx.annotation.LocalizeValue;

//TODO test localize arguments normal fxmlloader
public class TestController {

    @LocalizeValue(id = "btn", property = "numberOne")
    private final IntegerProperty property;
    @LocalizeValue(id = "btn", property = "numberTwo")
    private final IntegerProperty property2;
    @LocalizeValue(id = "chart", property = "title") //TODO check if empty
    private final FloatProperty charProperty;
    @FXML
    private AreaChart<Number, Number> chart;
    @FXML
    private TestComponent btn;

    public TestController() {
        property = new SimpleIntegerProperty(42);
        property2 = new SimpleIntegerProperty(100000);
        charProperty = new SimpleFloatProperty(55.6F);
    }

    @FXML
    private void initialize() {

        System.out.println(btn);


    }

}
