package com.akolov.notipy.jul;


import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * Initializes Java.util.Logging eith the content of log-debug.properties
 * To use this settings:
 * -Djava.util.logging.config.class=com.akolov.notipy.jul.LogDebug
 */
public class LogDebug {

    private static final Logger LOG = Logger.getLogger(LogDebug.class.getName());

    public LogDebug() {

        LogManager manager = LogManager.getLogManager();
        InputStream is = LogDebug.class.getResourceAsStream("/log-debug.properties");
        try {
            manager.readConfiguration(is);
        } catch (IOException e) {
            System.out.println("Log Debug failed");
        }
    }

}
