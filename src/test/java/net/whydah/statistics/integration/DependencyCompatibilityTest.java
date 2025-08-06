// 1. Dependency Verification Test
package net.whydah.statistics.integration;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;

/**
 * Test to verify that all required Jersey-Spring integration classes are available on classpath.
 * This catches NoClassDefFoundError issues early before they cause runtime failures.
 */
public class DependencyCompatibilityTest {
    private static final Logger log = LoggerFactory.getLogger(DependencyCompatibilityTest.class);

    @Test
    public void testJerseySpringIntegrationClassesAvailable() {
        // Test the specific class that was failing in the original error
        assertClassExists("org.glassfish.jersey.server.spring.SpringComponentProvider");
        assertClassExists("org.glassfish.jersey.server.spring.SpringComponentProvider$SpringManagedBeanFactory");

        // Test other critical Jersey-Spring classes
        assertClassExists("org.glassfish.jersey.ext.spring6.SpringBridge");
        assertClassExists("org.glassfish.hk2.spring.bridge.api.SpringBridge");
        assertClassExists("org.springframework.context.ApplicationContext");
        assertClassExists("org.glassfish.jersey.servlet.ServletContainer");
    }

    @Test
    public void testSpringFrameworkVersion() {
        try {
            Class<?> springVersionClass = Class.forName("org.springframework.core.SpringVersion");
            String version = (String) springVersionClass.getMethod("getVersion").invoke(null);
            log.info("Spring Framework version: {}", version);

            // Verify we have expected version range
            assertNotNull("Spring version should not be null", version);
            assertTrue("Spring version should be 6.x", version.startsWith("6."));
        } catch (Exception e) {
            fail("Could not determine Spring Framework version: " + e.getMessage());
        }
    }

    @Test
    public void testJerseyVersion() {
        try {
            // Jersey doesn't have a simple version class, so we'll check a core class
            Class<?> jerseyClass = Class.forName("org.glassfish.jersey.server.ApplicationHandler");
            Package jerseyPackage = jerseyClass.getPackage();
            String version = jerseyPackage.getImplementationVersion();
            log.info("Jersey version: {}", version);

            assertNotNull("Jersey package should have version info", version);
        } catch (Exception e) {
            fail("Could not determine Jersey version: " + e.getMessage());
        }
    }

    private void assertClassExists(String className) {
        try {
            Class.forName(className);
            log.debug("✓ Class found: {}", className);
        } catch (ClassNotFoundException e) {
            fail("Required class not found on classpath: " + className + ". This indicates a dependency issue.");
        }
    }
}