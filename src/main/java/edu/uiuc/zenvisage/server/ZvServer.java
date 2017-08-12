package edu.uiuc.zenvisage.server;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZvServer {

    private static Logger log = LoggerFactory.getLogger(ZvServer.class);
    private Server server;
    private static int port = 8080;

    public void setPort(int port) {
        this.port = port;
    }

    public void start() throws Exception {
        server = new Server(port);

        WebAppContext webAppContext = new WebAppContext();
        webAppContext.setContextPath("/");
        webAppContext.setWar("G:/zenvisage-0.1/target/zenvisage.war");
        webAppContext.setParentLoaderPriority(true);
        webAppContext.setServer(server);
        webAppContext.setClassLoader(ClassLoader.getSystemClassLoader());
        webAppContext.getSessionHandler().getSessionManager()
                .setMaxInactiveInterval(10);
        server.setHandler(webAppContext);
        server.start();
    }

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
//		createMetaTables();
        ZvServer zvServer = new ZvServer();
//		zvServer.loadDemoDatasets();
        zvServer.start();
    }

}
