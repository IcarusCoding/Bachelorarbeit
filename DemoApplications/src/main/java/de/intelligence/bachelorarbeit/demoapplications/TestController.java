package de.intelligence.bachelorarbeit.demoapplications;

import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.fxml.FXML;
import javafx.scene.chart.AreaChart;
import javafx.scene.control.Button;

import de.intelligence.bachelorarbeit.simplifx.annotation.PostConstruct;
import de.intelligence.bachelorarbeit.simplifx.controller.Controller;
import de.intelligence.bachelorarbeit.simplifx.controller.OnDestroy;
import de.intelligence.bachelorarbeit.simplifx.localization.LocalizeValue;

@Controller(fxml = "/test.fxml", css = "test.css")
public class TestController {

    @LocalizeValue(id = "chart", property = "title")
    private final FloatProperty charProperty;

    @LocalizeValue(id = "chart", index = 1, property = "title")
    private final FloatProperty charProperty2;

    @FXML
    private AreaChart<Number, Number> chart;

    @FXML
    private Button btn;

    public TestController() {
        charProperty = new SimpleFloatProperty(55.6F);
        charProperty2 = new SimpleFloatProperty(888888.8F);
    }

    @PostConstruct
    private void afterConstruction() {
        System.out.println(charProperty);
        System.out.println("TestController: Post construct!");
    }

    @OnDestroy
    private void onDestruction() {
        System.out.println("DESTRUCTION");
    }

    @FXML
    private void initialize() {
        btn.setOnAction(e -> {
            charProperty.setValue(charProperty.get() + Math.random());
            charProperty2.setValue(charProperty2.get() + Math.random());
        });
    }

}
