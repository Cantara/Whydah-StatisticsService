package net.whydah.statistics;

import org.eclipse.jetty.server.CustomRequestLog;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.RequestLogHandler;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.valuereporter.ValuereporterException;
import org.valuereporter.helper.DatabaseMigrationHelper;
import org.valuereporter.helper.EmbeddedDatabaseHelper;
import org.valuereporter.helper.StatusType;

import java.io.File;
import java.net.BindException;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.LogManager;


public class Main {
    public static final int DEFAULT_PORT_NO = 4901;
    public static final String CONTEXT_PATH = "/reporter";
    private static final Logger log = LoggerFactory.getLogger(Main.class);
    private static final java.lang.String DATABASE_URL = "jdbc.url";

    private Server server;
    private String resourceBase;
    private int jettyPort = -1;

    static {
        System.out.println("Main class is being loaded");
    }

    public Main() {


        Properties resources = PropertiesHelper.findProperties();

        // Log all loaded properties for debugging
        logAllProperties(resources);

        try{
            new EmbeddedDatabaseHelper(resources).initializeDatabase();
            if (useLocalDatabase(resources) && DatabaseMigrationHelper.isFlywaySupported(resources)) {
                log.info("Trying to upgrade database with Flyway");
                new DatabaseMigrationHelper(resources).upgradeDatabase();
            }
            jettyPort = PropertiesHelper.findHttpPort(resources);
        } catch (ValuereporterException tde) {
            log.error("Could not initalize the service. Exiting. ", tde);
            System.exit(0);
        } catch (Exception e) {
            log.error("Unexpected error. Exiting. ", e);
            System.exit(0);
        }

        server = new Server(jettyPort);

        URL url = ClassLoader.getSystemResource("webapp/WEB-INF/web.xml");
        resourceBase = url.toExternalForm().replace("/WEB-INF/web.xml", "");
    }

    // Helper method to log all properties
    private void logAllProperties(Properties properties) {
        System.out.println("Main method starting");
        log.info("======= LOADED PROPERTIES =======");
        // Log critical properties with extra visibility
        logCriticalProperty(properties, "jdbc.useEmbedded");
        logCriticalProperty(properties, "jdbc.setupNewDb");
        logCriticalProperty(properties, "jdbc.driverClassName");
        logCriticalProperty(properties, "jdbc.url");
        logCriticalProperty(properties, "jdbc.username");
        logCriticalProperty(properties, "jetty.http.port");

        // Log all other properties
        properties.stringPropertyNames().stream()
                .sorted()
                .forEach(key -> log.debug("Property: {} = {}", key, properties.getProperty(key)));
        log.info("================================");
    }

    // Log critical properties with higher visibility
    private void logCriticalProperty(Properties properties, String key) {
        String value = properties.getProperty(key);
        log.info("CRITICAL Property: {} = {}", key, value);
    }

    public static void main(String[] arguments) throws Exception {

        try {
            System.out.println("About to reset LogManager");
            LogManager.getLogManager().reset();
            System.out.println("LogManager reset successful");

            System.out.println("About to remove handlers for root logger");
            SLF4JBridgeHandler.removeHandlersForRootLogger();
            System.out.println("Removed handlers successfully");

            System.out.println("About to install SLF4JBridgeHandler");
            SLF4JBridgeHandler.install();
            System.out.println("SLF4JBridgeHandler installed successfully");

            System.out.println("About to set logger level");
            LogManager.getLogManager().getLogger("").setLevel(Level.FINEST);
            System.out.println("Logger level set successfully");
        } catch (Exception e) {
            System.out.println("Error in logging setup: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("Creating Main instance");
        Main main = new Main();

        try {
            System.out.println("Calling main.start()");
            main.start();
            System.out.println("main.start() completed successfully");

            System.out.println("Calling main.join()");
            main.join();
            System.out.println("main.join() completed");
        } catch (Exception e) {
            System.out.println("Error in main execution: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void start() throws RuntimeException {
        WebAppContext context = new WebAppContext();
        log.debug("Start Jetty using resourcebase={}", resourceBase);
        context.setDefaultsDescriptor(null); // Don't use the default web.xml
        context.setInitParameter("org.eclipse.jetty.servlet.Default.dirAllowed", "false");

        context.setDescriptor(resourceBase + "/WEB-INF/web.xml");
        context.setResourceBase(resourceBase);
        context.setContextPath(CONTEXT_PATH);
        context.setParentLoaderPriority(true);
        context.setAttribute("org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern",".*/[^/]*jstl.*\\.jar$");

        // Update the ClassList configuration to use the newer API
        context.setConfigurationClasses(new String[]{
                "org.eclipse.jetty.webapp.WebInfConfiguration",
                "org.eclipse.jetty.webapp.WebXmlConfiguration",
                "org.eclipse.jetty.webapp.MetaInfConfiguration",
                "org.eclipse.jetty.webapp.FragmentConfiguration",
                "org.eclipse.jetty.plus.webapp.EnvConfiguration",
                "org.eclipse.jetty.plus.webapp.PlusConfiguration",
                "org.eclipse.jetty.annotations.AnnotationConfiguration",
                "org.eclipse.jetty.webapp.JettyWebXmlConfiguration"
        });

        HandlerCollection handlers = new HandlerCollection();
        RequestLogHandler requestLogHandler = new RequestLogHandler();
        handlers.setHandlers(new Handler[]{context,new DefaultHandler(),requestLogHandler, new HealthHandler()});
        server.setHandler(handlers);


        enableRequestLogging(requestLogHandler);

        try {
            server.start();
        } catch (BindException be) {
            throw new ValuereporterException("Failed to start the server. Ther port is already in use.", StatusType.RETRY_NOT_POSSIBLE);
        } catch (Exception e) {
            throw new ValuereporterException("Failed to start." ,e, StatusType.RETRY_NOT_POSSIBLE);
        }
        Throwable springStartupFailed = context.getUnavailableException();
        if (springStartupFailed != null) {
            throw new ValuereporterException("Failed to initialize Spring Application Context." , springStartupFailed, StatusType.RETRY_NOT_POSSIBLE);
        }
        int localPort = getPortNumber();
        log.info("Jetty server started on port {}, context path {}", localPort, CONTEXT_PATH);
    }

    private void enableRequestLogging(RequestLogHandler requestLogHandler) {
        String logDir = "./logs";
        ensureLogDirexist(logDir);

        // Create a new CustomRequestLog with the NCSA format
        // The constructor takes different parameters in the newer API
        // and the configuration is done differently
        CustomRequestLog requestLog = new CustomRequestLog(logDir + "/jetty-yyyy_mm_dd.request.log", CustomRequestLog.NCSA_FORMAT);

        // These setter methods are no longer available in CustomRequestLog
        // We need to configure these using a RequestLog.Writer or other means
        // For now, let's use the basic configuration to get it working

        requestLogHandler.setRequestLog(requestLog);
    }

    private void ensureLogDirexist(String logDirPath) {
        File logDir = new File(logDirPath);
        if (!logDir.exists()) {
            logDir.mkdir();
        }
    }

    public void stopOnError() throws Exception {
        server.stop();
        System.exit(1);
    }

    public void join() throws InterruptedException {
        server.join();
    }
    private boolean useLocalDatabase(Properties resources) {
        boolean useEmbedded = EmbeddedDatabaseHelper.useEmbeddedDb(resources);
        boolean useLocal = !useEmbedded;
        return useLocal;
    }

    public int getPortNumber() {
        return ((ServerConnector) server.getConnectors()[0]).getLocalPort();
    }

    public void setResourceBase(String resourceBase) {
        this.resourceBase = resourceBase;
    }

    public String getResourceBase() {
        return resourceBase;
    }
}