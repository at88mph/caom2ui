package ca.nrc.cadc.search.integration;

import java.net.URL;
import java.util.Objects;

public class TestConfig {
    static URL getBaseURL() {
        try {
            final String fromEnv = System.getenv("ADVANCED_SEARCH_UI_BASE_URL");
            return new URL(Objects.requireNonNullElse(fromEnv, "http://localhost:8080/AdvancedSearch/"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
