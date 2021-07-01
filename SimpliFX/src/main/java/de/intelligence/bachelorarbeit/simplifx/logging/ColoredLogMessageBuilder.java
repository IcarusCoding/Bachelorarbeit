package de.intelligence.bachelorarbeit.simplifx.logging;

import java.text.DateFormat;
import java.util.Date;

public class ColoredLogMessageBuilder {

    private final StringBuilder builder;

    public ColoredLogMessageBuilder() {
        this.builder = new StringBuilder();
    }

    public String build() {
        return this.builder.toString();
    }

    public ColoredLogMessageBuilder add(String message) {
        this.builder.append(message);
        return this;
    }

    public ColoredLogMessageBuilder add(String message, Color color) {
        this.builder.append(color).append(message).append(Color.RESET);
        return this;
    }

    public ColoredLogMessageBuilder addDate(long millis, DateFormat format, Color color) {
        this.builder.append(color).append(format.format(new Date(millis))).append(Color.RESET);
        return this;
    }

    public ColoredLogMessageBuilder addDate(long millis, DateFormat format) {
        this.builder.append(format.format(new Date(millis)));
        return this;
    }

    public ColoredLogMessageBuilder endLine() {
        this.builder.append("\n");
        return this;
    }

}
