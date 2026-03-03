package ca.nrc.cadc.search.integration;

import ca.nrc.cadc.net.HttpGet;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONTokener;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;


public class UnitConversionServletTest {
    private static final Logger LOGGER = Logger.getLogger(UnitConversionServletTest.class);
    private static final String ENDPOINT = "unitconversion";

    static {
        LOGGER.setLevel(Level.DEBUG);
    }

    private static void assertOutput(final String path, final String expected) throws Exception {
        final URL url = new URL(TestConfig.getBaseURL(), Path.of(UnitConversionServletTest.ENDPOINT, path).toString());
        LOGGER.debug("URL: " + url);
        final HttpGet httpGet = new HttpGet(url, true);
        httpGet.run();

        try (final InputStream inputStream = httpGet.getInputStream()) {
            final JSONTokener jsonTokener = new JSONTokener(inputStream);
            final JSONArray jsonArray = new JSONArray(jsonTokener);

            Assertions.assertEquals(1, jsonArray.length());
            Assertions.assertEquals(expected, jsonArray.getString(0).trim(), "Wrong term");
        }
    }

    @Test
    public void testGetConversions() throws Exception {
        UnitConversionServletTest.assertOutput("Plane.energy.bounds.samples?term=66A", "(= 6.600E-9 metres)");
        UnitConversionServletTest.assertOutput("Plane.time.exposure?term=14h", "(= 50400.0 seconds)");
        UnitConversionServletTest.assertOutput("Plane.position.sampleSize?term=10..20", "(10.0..20.0 arcseconds)");
        UnitConversionServletTest.assertOutput("Plane.energy.resolvingPower?term=%3E+40000", "> 40000");
        UnitConversionServletTest.assertOutput("Plane.energy.restwav?term=0.88GHz", "(= 3.407E-1 metres)");
    }
}
