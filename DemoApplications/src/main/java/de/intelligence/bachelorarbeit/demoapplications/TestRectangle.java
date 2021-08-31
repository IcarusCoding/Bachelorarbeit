package de.intelligence.bachelorarbeit.demoapplications;

import java.util.List;

import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableObjectProperty;
import javafx.css.converter.SizeConverter;
import javafx.scene.shape.Rectangle;

import de.intelligence.bachelorarbeit.simplifx.css.CssProperty;

// This class is not used in the demo application and is just a demonstration of the implemented css features.
public final class TestRectangle extends Rectangle {

    @CssProperty(property = "-fx-width", localPropertyField = "localWidthProperty", converterClass = SizeConverter.class, bindTo = "width")
    @CssProperty(property = "-fx-height", localPropertyField = "localHeightProperty", converterClass = SizeConverter.class, bindTo = "height")
    public static List<CssMetaData<? extends Styleable, ?>> CLASS_CSS_META_DATA;

    private StyleableObjectProperty<Double> localWidthProperty;
    private StyleableObjectProperty<Double> localHeightProperty;

    public TestRectangle() {
        super();
    }

    public TestRectangle(double width, double height) {
        super(width, height);
    }

    @Override
    public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
        return CLASS_CSS_META_DATA;
    }

}