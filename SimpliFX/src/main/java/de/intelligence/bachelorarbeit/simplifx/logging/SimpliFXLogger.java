package de.intelligence.bachelorarbeit.simplifx.logging;

import java.text.SimpleDateFormat;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

//TODO maybe switch to log4j (extending from Layout)
public class SimpliFXLogger {

    private static final ConcurrentHashMap<Long, String> threadCache = new ConcurrentHashMap<>();

    private final Logger logger;

    public SimpliFXLogger(Logger logger) {
        this.logger = logger;
    }

    public static SimpliFXLogger create(Class<?> clazz) {
        return new SimpliFXLogger(SimpliFXLogger.createLogger(clazz));
    }

    private static Logger createLogger(Class<?> clazz) {
        final Logger logger = Logger.getLogger(clazz.getSimpleName());
        logger.setUseParentHandlers(false);
        final ConsoleHandler consoleHandler = new ConsoleHandler();
        final Formatter formatter = new Formatter() {
            @Override
            public String format(LogRecord record) {
                final LogLevel currentLogLevel = LogLevel.convert(record.getLevel());
                final StackTraceElement callerElem = Thread.currentThread().getStackTrace()[9];
                return new ColoredLogMessageBuilder()
                        .add("[", Color.BYELLOW)
                        .addDate(record.getMillis(), new SimpleDateFormat("HH:mm:ss"), Color.BWHITE)
                        .add(" - ", Color.BYELLOW)
                        .add(SimpliFXLogger.getThreadName(record.getLongThreadID()), Color.BWHITE)
                        .add("] ", Color.BYELLOW)
                        .add(currentLogLevel.name(), currentLogLevel.getColor())
                        .add(" (", Color.BYELLOW)
                        .add(SimpliFXLogger.getSimpleClassName(callerElem.getClassName()) + ":" + callerElem
                                .getLineNumber(), Color.BWHITE)
                        .add(")", Color.BYELLOW)
                        .add(" " + record.getMessage(), currentLogLevel.getTextColor())
                        .endLine()
                        .build();
            }
        };
        consoleHandler.setFormatter(formatter);
        logger.addHandler(consoleHandler);
        return logger;
    }

    private static String getSimpleClassName(String fullyQualified) {
        int lastIdx = fullyQualified.lastIndexOf('.');
        if (lastIdx == -1) {
            return fullyQualified;
        }
        return fullyQualified.substring(lastIdx + 1);
    }

    private static String getThreadName(long id) {
        if (!SimpliFXLogger.threadCache.containsKey(id)) {
            for (final Thread thread : Thread.getAllStackTraces().keySet()) {
                if (id == thread.getId()) {
                    SimpliFXLogger.threadCache.put(id, thread.getName());
                    return thread.getName();
                }
            }
            return "Unknown";
        }
        return SimpliFXLogger.threadCache.get(id);
    }

    public void info(String message) {
        this.logger.info(message);
    }

    public void warn(String message) {
        this.logger.warning(message);
    }

    public void error(String message) {
        this.logger.severe(message);
    }

}
