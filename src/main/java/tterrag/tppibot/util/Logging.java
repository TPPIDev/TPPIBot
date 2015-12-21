package tterrag.tppibot.util;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Appender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.SimpleLayout;

import tterrag.tppibot.config.Config;

import com.google.common.base.Throwables;

public class Logging {

    private static final Logger logger = LogManager.getLogger("TPPIBot");

    public static final File logsDir;
    private static final File logFile;

    static {
        SimpleLayout layout = new SimpleLayout();
        logsDir = new File(Config.baseDir, "logs");
        logsDir.mkdir();
        logFile = new File(logsDir, "latest.log");
        try {
            Appender appender = new FileAppender(layout, logFile.getPath(), false);
            appender.setLayout(new PatternLayout("%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%L - %m%n"));
            logger.addAppender(appender);
        } catch (IOException e) {
            Throwables.propagate(e);
        }
    }

    public static void log(String message) {
        logger.info(message);
    }

    public static void debug(String message) {
        logger.debug(message);
    }

    public static void error(String message) {
        logger.error(message);
    }
}
