package net.whydah.statistics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.valuereporter.ValuereporterException;
import org.valuereporter.ValuereporterTechnicalException;
import org.valuereporter.helper.StatusType;

import java.util.Properties;

public class PropertiesHelper {
    private static final Logger log = LoggerFactory.getLogger(PropertiesHelper.class);

    public static Properties findProperties() {
        Properties properties = new Properties();
        properties.putAll(Configuration.getMap());
        return properties;
    }
    public static int findHttpPort(Properties resoruces) throws ValuereporterException {
        int retPort = -1;
        String httpPort = resoruces.getProperty("jetty.http.port");

        if (httpPort == null || httpPort.length() == 0) {
            log.info("jetty.http.port missing. Will use default port {}", org.valuereporter.Main.DEFAULT_PORT_NO);
            retPort = org.valuereporter.Main.DEFAULT_PORT_NO;
        } else {
            try {
                retPort = Integer.valueOf(httpPort).intValue();
            } catch (NumberFormatException nfe) {
                log.error("Could not convert {} to int. No jetty port is set.", httpPort);
                throw new ValuereporterTechnicalException("Proprerty 'jetty.http.port' with value "+ httpPort +" could not be cast to int.", StatusType.RETRY_NOT_POSSIBLE);
            }
        }
        return retPort;
    }
}
