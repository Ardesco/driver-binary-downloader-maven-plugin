package com.lazerycode.selenium;

import org.apache.log4j.AppenderSkeleton;
import org.apache.logging.log4j.Level;
import org.apache.maven.plugin.logging.Log;

class MavenLoggerLog4jBridge extends AppenderSkeleton {
    private final Log LOG;

    public MavenLoggerLog4jBridge(Log logger) {
        this.LOG = logger;
    }

    @Override
    protected void append(org.apache.log4j.spi.LoggingEvent event) {
        int level = event.getLevel().toInt();
        String msg = event.getMessage().toString();
        if (level == Level.DEBUG.intLevel() || level == Level.TRACE.intLevel()) {
            this.LOG.debug(msg);
        } else if (level == Level.INFO.intLevel()) {
            this.LOG.info(msg);
        } else if (level == Level.WARN.intLevel()) {
            this.LOG.warn(msg);
        } else if (level == Level.ERROR.intLevel() || level == Level.FATAL.intLevel()) {
            this.LOG.error(msg);
        }
    }

    public void close() {
    }

    public boolean requiresLayout() {
        return false;
    }
}
