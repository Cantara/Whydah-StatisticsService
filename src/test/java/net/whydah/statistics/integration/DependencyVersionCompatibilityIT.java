// 5. Maven Failsafe Integration Test
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
        verifyJerseySpringBridge();
        verifySpringFrameworkCore();
        verifyHK2Integration();

        log.info("✓ All dependency compatibility checks passed");
    }

    private void verifyJerseySpringBridge() {
        try {
            Class.forName("org.glassfish.jersey.ext.spring6.SpringBridge");
            Class.forName("org.glassfish.hk2.spring.bridge.api.SpringBridge");
            log.info("✓ Jersey-Spring bridge classes are available");
        } catch (ClassNotFoundException e) {
            fail("Jersey-Spring bridge is not properly configured: " + e.getMessage());
        }
    }

    private void verifySpringFrameworkCore() {
        try {
            Class.forName("org.springframework.context.ApplicationContext");
            Class.forName("org.springframework.beans.factory.BeanFactory");
            log.info("✓ Spring Framework core classes are available");
        } catch (ClassNotFoundException e) {
            fail("Spring Framework core is not properly configured: " + e.getMessage());
        }
    }

    private void verifyHK2Integration() {
        try {
            Class.forName("org.glassfish.hk2.api.ServiceLocator");
            Class.forName("org.glassfish.hk2.spring.bridge.api.SpringBridge");
            log.info("✓ HK2 integration classes are available");
        } catch (ClassNotFoundException e) {
            fail("HK2 integration is not properly configured: " + e.getMessage());
        }
    }
}