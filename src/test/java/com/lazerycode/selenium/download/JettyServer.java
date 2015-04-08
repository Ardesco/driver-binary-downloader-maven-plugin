package com.lazerycode.selenium.download;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

public class JettyServer {

    private Server jettyServer;

    public void startJettyServer(int port) throws Exception {
        String resourceBasePath = this.getClass().getResource("/jetty").toExternalForm();
        jettyServer = new Server(port);
        WebAppContext webapp = new WebAppContext();
        webapp.setResourceBase(resourceBasePath);
        jettyServer.setHandler(webapp);
        jettyServer.start();
    }

    public void stopJettyServer() throws Exception {
        jettyServer.stop();
    }
}
