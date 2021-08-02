package de.intelligence.bachelorarbeit.demoapplications;

import java.util.List;

import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleablePropertyFactory;
import javafx.css.converter.SizeConverter;
import javafx.scene.shape.Rectangle;

import de.intelligence.bachelorarbeit.simplifx.css.CssProperty;

public final class TestRectangle extends Rectangle {

    private static final StyleablePropertyFactory<TestRectangle> factory = new StyleablePropertyFactory<>(Rectangle.getClassCssMetaData());
    //TODO support null localProperty and only use temp vars then
    @CssProperty(property = "-fx-width", localPropertyField = "testius", converterClass = SizeConverter.class, bindTo = "width")
    @CssProperty(property = "-fx-height", localPropertyField = "testius2", converterClass = SizeConverter.class, bindTo = "height")
    //@CssProperty(property = "-fx-height", localPropertyField = "styleableHeight", converterClass = SizeConverter.class)
    public static List<CssMetaData<? extends Styleable, ?>> CLASS_CSS_META_DATA;
    private StyleableObjectProperty<Double> testius;
    private StyleableObjectProperty<String> testius2;

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