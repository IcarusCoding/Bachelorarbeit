package de.intelligence.bachelorarbeit.demoapplications;

import java.util.List;

import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableDoubleProperty;
import javafx.css.converter.SizeConverter;
import javafx.scene.shape.Rectangle;

import de.intelligence.bachelorarbeit.simplifx.annotation.CssProperty;

public final class TestRectangle extends Rectangle {

    //TODO maybe map for property -> meta data
    //TODO maybe remove localPropert ability completely and bind to properties from superclass
    @CssProperty(property = "-fx-width", localPropertyField = "styleableWidth", converterClass = SizeConverter.class)
    @CssProperty(property = "-fx-height", localPropertyField = "styleableHeight", converterClass = SizeConverter.class)
    public static List<CssMetaData<? extends Styleable, ?>> CLASS_CSS_META_DATA;

    private StyleableDoubleProperty styleableWidth;
    private StyleableDoubleProperty styleableHeight;

    //TODO maybe remove from fxmlloader and do it manually bc no binding is possible
    public TestRectangle() {
        super();
        // StyleBootstrap.init(this, Rectangle.getClassCssMetaData()); // temporary solution
        //System.out.println(styleableWidth);
        //System.out.println(styleableHeight);
    }

    public TestRectangle(double width, double height) {
        super(width, height);
        this.widthProperty().bind(this.styleableWidth);
        this.heightProperty().bind(this.styleableHeight);
    }

    @Override
    public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
        return CLASS_CSS_META_DATA;
    }

}