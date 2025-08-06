// 4. Application Startup Test
package net.whydah.statistics.integration;

import net.whydah.statistics.Main;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.HttpURLConnection;
import java.net.URL;

import static org.junit.Assert.*;

/**
 * Full integration test that starts the actual application and verifies it works.
 */
public class ApplicationStartupTest {
    private static final Logger log = LoggerFactory.getLogger(ApplicationStartupTest.class);
    private Main main;
    private Thread serverThread;

    @Before
    public void setUp() {
        // Set test properties to avoid conflicts
        System.setProperty("jetty.http.port", "0"); // Use random port
        System.setProperty("jdbc.useEmbedded", "true");
    }

    @Test(timeout = 30000) // 30 second timeout
    public void testApplicationStartsSuccessfully() throws Exception {
        main = new Main();

        serverThread = new Thread(() -> {
            try {
                main.start();
            } catch (Exception e) {
                log.error("Failed to start application in test", e);
                fail("Application failed to start: " + e.getMessage());
            }
        });

        serverThread.start();

        // Wait for server to start
        int maxAttempts = 15;
        int port = -1;
        for (int i = 0; i < maxAttempts; i++) {
            try {
                port = main.getPortNumber();
                if (port > 0) {
                    break;
                }
                Thread.sleep(1000);
            } catch (Exception e) {
                Thread.sleep(1000);
            }
        }

        assertTrue("Server should have started and have a port number", port > 0);
        log.info("✓ Application started successfully on port {}", port);

        // Test health endpoint
        testHealthEndpoint(port);
    }

    private void testHealthEndpoint(int port) throws Exception {
        URL healthUrl = new URL("http://localhost:" + port + "/reporter/health");
        HttpURLConnection connection = (HttpURLConnection) healthUrl.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);

        int responseCode = connection.getResponseCode();
        assertEquals("Health endpoint should return 200", 200, responseCode);
        log.info("✓ Health endpoint responded successfully");
    }

    @After
    public void tearDown() throws Exception {
        if (main != null) {
            try {
                main.stopOnError();
            } catch (Exception e) {
                log.warn("Error stopping server", e);
            }
        }
        if (serverThread != null && serverThread.isAlive()) {
            serverThread.interrupt();
            serverThread.join(5000);
        }
    }
}