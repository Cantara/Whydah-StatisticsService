package net.whydah.statistics.integration;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.fail;

/**
 * Integration test that runs during Maven's integration-test phase.
 * Filename ends with 'IT' to be picked up by maven-failsafe-plugin.
 */
public class DependencyVersionCompatibilityIT {
    private static final Logger log = LoggerFactory.getLogger(DependencyVersionCompatibilityIT.class);

    @Test
    public void verifyAllCriticalDependenciesAreCompatible() {
        log.info("Running dependency compatibility verification...");

        // Run all critical checks in one integration test
        verifyJerseySpringIntegration();
        verifySpringFrameworkCore();
        verifyJacksonIntegration();

        log.info("✓ All dependency compatibility checks passed");
    }

    private void verifyJerseySpringIntegration() {
        try {
            // Test the actual Jersey-Spring integration classes that exist and work
            Class.forName("org.glassfish.jersey.server.spring.SpringComponentProvider");
            Class.forName("org.glassfish.jersey.servlet.ServletContainer");
            // REMOVED the non-existent class: org.glassfish.jersey.ext.spring6.SpringComponentProvider
            log.info("✓ Jersey-Spring integration classes are available");
        } catch (ClassNotFoundException e) {
            fail("Jersey-Spring integration is not properly configured: " + e.getMessage());
        }
    }

    private void verifySpringFrameworkCore() {
        try {
            Class.forName("org.springframework.context.ApplicationContext");
            Class.forName("org.springframework.beans.factory.BeanFactory");
            Class.forName("org.springframework.beans.factory.annotation.Autowired");
            Class.forName("org.springframework.stereotype.Component");
            log.info("✓ Spring Framework core classes are available");
        } catch (ClassNotFoundException e) {
            fail("Spring Framework core is not properly configured: " + e.getMessage());
        }
    }

    private void verifyJacksonIntegration() {
        try {
            Class.forName("com.fasterxml.jackson.databind.ObjectMapper");
            Class.forName("org.glassfish.jersey.media.json.jackson.JacksonFeature");
            log.info("✓ Jackson JSON integration classes are available");
        } catch (ClassNotFoundException e) {
            fail("Jackson integration is not properly configured: " + e.getMessage());
        }
    }

    @Test
    public void verifyDatabaseIntegration() {
        try {
            Class.forName("org.springframework.jdbc.core.JdbcTemplate");
            Class.forName("org.springframework.jdbc.datasource.DriverManagerDataSource");
            log.info("✓ Database integration classes are available");
        } catch (ClassNotFoundException e) {
            fail("Database integration is not properly configured: " + e.getMessage());
        }
    }

    @Test
    public void verifyServletContainerIntegration() {
        try {
            Class.forName("jakarta.servlet.http.HttpServletRequest");
            Class.forName("jakarta.servlet.http.HttpServletResponse");
            Class.forName("org.eclipse.jetty.server.Server");
            Class.forName("org.eclipse.jetty.webapp.WebAppContext");
            log.info("✓ Servlet container integration classes are available");
        } catch (ClassNotFoundException e) {
            fail("Servlet container integration is not properly configured: " + e.getMessage());
        }
    }
}