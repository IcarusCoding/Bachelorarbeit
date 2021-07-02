package de.intelligence.bachelorarbeit.simplifx.logging;

public enum Color {

    RESET("\u001B[0m"),
    BLACK("\u001B[30m"),
    RED("\u001B[31m"),
    GREEN("\u001B[32m"),
    YELLOW("\u001B[33m"),
    BLUE("\u001B[34m"),
    PURPLE("\u001B[35m"),
    CYAN("\u001B[36m"),
    WHITE("\u001B[37m"),
    GRAY("\u001B[90m"),
    BRED("\u001B[91m"),
    BGREEN("\u001B[92m"),
    BYELLOW("\u001B[93m"),
    BBLUE("\u001B[94m"),
    BMAGENTA("\u001B[95m"),
    BCYAN("\u001B[96m"),
    BWHITE("\u001B[97m");

    private final String color;

    Color(String color) {
        this.color = color;
    }

    public String getColor() {
        return this.color;
    }

}
