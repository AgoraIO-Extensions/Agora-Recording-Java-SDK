package io.agora.recording.example.utils;

import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class SampleLogger {
    private static boolean enableLog = true;

    private static final Logger logger = Logger.getLogger("RecorderSample");

    static {
        Logger rootLogger = Logger.getLogger("");
        Handler[] handlers = rootLogger.getHandlers();
        for (Handler handler : handlers) {
            rootLogger.removeHandler(handler);
        }

        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(Level.INFO);
        handler.setFormatter(new CustomFormatter());
        logger.addHandler(handler);
        logger.setUseParentHandlers(false);
    }

    public static void enableLog(boolean enable) {
        enableLog = enable;
    }

    public static void info(String message) {
        if (enableLog) {
            synchronized (logger) {
                logger.info("[" + Utils.getCurrentTime() + "] " + message);
            }
        }
    }

    public static void error(String message) {
        if (enableLog) {
            synchronized (logger) {
                logger.severe("[" + Utils.getCurrentTime() + "] " + message);
            }
        }
    }

    private static class CustomFormatter extends Formatter {
        @Override
        public String format(LogRecord record) {
            return record.getLevel().getName() + record.getMessage() + "\n";
        }
    }

}
