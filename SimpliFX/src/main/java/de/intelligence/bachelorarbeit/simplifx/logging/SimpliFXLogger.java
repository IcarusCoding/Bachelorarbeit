package de.intelligence.bachelorarbeit.simplifx.logging;

import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class SimpliFXLogger {

    private final Logger logger;

    public SimpliFXLogger(Logger logger) {
        this.logger = logger;
    }

    public static SimpliFXLogger create(Class<?> clazz) {
        return new SimpliFXLogger(SimpliFXLogger.createLogger(clazz));
    }

    private static Logger createLogger(Class<?> clazz) {
        final Logger logger = Logger.getLogger(clazz.getCanonicalName());
        logger.setUseParentHandlers(false);
        final ConsoleHandler consoleHandler = new ConsoleHandler();
        final Formatter formatter = new Formatter() {
            @Override
            public String format(LogRecord record) {
                return new ColoredLogMessageBuilder().add("test", Color.CYAN).endLine().build();
            }
        };
        consoleHandler.setFormatter(formatter);
        logger.addHandler(consoleHandler);
        return logger;
    }

    public void warn(String message) {
        this.logger.warning(message);
    }

}
