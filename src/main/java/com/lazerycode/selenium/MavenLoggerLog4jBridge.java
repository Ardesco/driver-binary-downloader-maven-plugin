package com.lazerycode.selenium;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.maven.plugin.logging.Log;

public class MavenLoggerLog4jBridge extends AppenderSkeleton {
    private Log LOG;

    public MavenLoggerLog4jBridge(Log logger) {
        this.LOG = logger;
    }

    protected void append(LoggingEvent event) {
        int level = event.getLevel().toInt();
        String msg = event.getMessage().toString();
        if (level == Level.DEBUG_INT || level == Level.TRACE_INT) {
            this.LOG.debug(msg);
        } else if (level == Level.INFO_INT) {
            this.LOG.info(msg);
        } else if (level == Level.WARN_INT) {
            this.LOG.warn(msg);
        } else if (level == Level.ERROR_INT || level == Level.FATAL_INT) {
            this.LOG.error(msg);
        }
    }

    public void close() {
    }

    public boolean requiresLayout() {
        return false;
    }
}