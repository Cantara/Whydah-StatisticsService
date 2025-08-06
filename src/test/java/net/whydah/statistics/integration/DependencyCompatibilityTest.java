package net.whydah.statistics.integration;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.fail;

/**
 * Test to verify that all required Jersey-Spring integration classes are available on classpath.
 * This catches NoClassDefFoundError issues early before they cause runtime failures.
 */
public class DependencyCompatibilityTest {
    private static final Logger log = LoggerFactory.getLogger(DependencyCompatibilityTest.class);

    @Test
    public void testJerseySpringIntegrationClassesAvailable() {
        // Test the actual classes that exist and work
        assertClassExists("org.glassfish.jersey.server.spring.SpringComponentProvider");
        assertClassExists("org.glassfish.jersey.servlet.ServletContainer");
        assertClassExists("org.glassfish.jersey.server.ApplicationHandler");

        log.info("✓ Jersey-Spring integration classes are available");
    }

    @Test
    public void testSpringFrameworkVersion() {
        try {
            // Test that Spring classes are available
            assertClassExists("org.springframework.context.ApplicationContext");
            assertClassExists("org.springframework.beans.factory.BeanFactory");
            log.info("✓ Spring Framework classes are available");
        } catch (Exception e) {
            fail("Could not verify Spring Framework availability: " + e.getMessage());
        }
    }

    @Test
    public void testJerseyVersion() {
        try {
            // Jersey doesn't have a simple version class, so we'll check a core class
            assertClassExists("org.glassfish.jersey.server.ApplicationHandler");
            log.info("✓ Jersey core classes are available");
        } catch (Exception e) {
            fail("Could not determine Jersey availability: " + e.getMessage());
        }
    }

    @Test
    public void testJacksonAvailability() {
        try {
            assertClassExists("com.fasterxml.jackson.databind.ObjectMapper");
            assertClassExists("com.fasterxml.jackson.core.JsonParser");
            log.info("✓ Jackson JSON processing classes are available");
        } catch (Exception e) {
            fail("Could not verify Jackson availability: " + e.getMessage());
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