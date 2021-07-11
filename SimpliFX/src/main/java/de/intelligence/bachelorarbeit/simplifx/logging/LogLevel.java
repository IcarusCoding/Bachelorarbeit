package de.intelligence.bachelorarbeit.simplifx.logging;

import java.util.logging.Level;

public enum LogLevel {

    ALL(Color.RESET, Color.RESET),
    TRACE(Color.WHITE, Color.WHITE),
    DEBUG(Color.WHITE, Color.WHITE),
    INFO(Color.BGREEN, Color.BGREEN),
    WARN(Color.YELLOW, Color.YELLOW),
    ERROR(Color.BRED, Color.BRED);

    private final Color color;
    private final Color textColor;

    LogLevel(Color color, Color textColor) {
        this.color = color;
        this.textColor = textColor;
    }

    public static LogLevel convert(Level level) {
        if (level == Level.WARNING) {
            return LogLevel.WARN;
        }
        if (level == Level.SEVERE) {
            return LogLevel.ERROR;
        }
        if (level == Level.INFO) {
            return LogLevel.INFO;
        }
        return LogLevel.ALL;
    }

    public Color getColor() {
        return this.color;
    }

    public Color getTextColor() {
        return this.textColor;
    }

}
