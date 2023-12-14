package net.whydah.statistics.util;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertTrue;

public class DBDriverTest {

    private static final Logger log = LoggerFactory.getLogger(DBDriverTest.class);
    private final String jdbcDriverClassName = "";
    private final String jdbcUrl = "";
    private final String jdbcUserName = "";
    private final String jdbcPassword = "";
    private final String jdbcAdminPassword = "";

    @Test
    public void testHSQLDB() {
        boolean isExistingDb = false;


        String hsqldbUrl = this.jdbcUrl + ";ifexists=true";
        log.info("Try to connect to existing database with url {}", hsqldbUrl);

        try {
            Class.forName("org.hsqldb.jdbc.JDBCDriver");
        } catch (Exception e) {
            System.err.println("ERROR: failed to load HSQLDB JDBC driver.");
            e.printStackTrace();
            assertTrue(e == null);

        }

        //DriverManager.getConnection(hsqldbUrl, this.jdbcUserName, this.jdbcPassword);
        //isExistingDb = true;
    }

    @Test
    public void testMariaDB() {
        boolean isExistingDb = false;


        String mariaUrl = this.jdbcUrl + ";ifexists=true";
        log.info("Try to connect to existing database with url {}", mariaUrl);

        try {
            Class.forName("org.mariadb.jdbc.Driver");
        } catch (Exception e) {
            System.err.println("ERROR: failed to load Maria JDBC driver.");
            e.printStackTrace();
            assertTrue(e == null);

        }

        //DriverManager.getConnection(hsqldbUrl, this.jdbcUserName, this.jdbcPassword);
        //isExistingDb = true;
    }
}
