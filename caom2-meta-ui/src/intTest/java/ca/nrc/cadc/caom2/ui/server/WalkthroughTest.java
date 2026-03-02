/*
 ************************************************************************
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 *
 * (c) 2013.                         (c) 2013.
 * National Research Council            Conseil national de recherches
 * Ottawa, Canada, K1A 0R6              Ottawa, Canada, K1A 0R6
 * All rights reserved                  Tous droits reserves
 *
 * NRC disclaims any warranties         Le CNRC denie toute garantie
 * expressed, implied, or statu-        enoncee, implicite ou legale,
 * tory, of any kind with respect       de quelque nature que se soit,
 * to the software, including           concernant le logiciel, y com-
 * without limitation any war-          pris sans restriction toute
 * ranty of merchantability or          garantie de valeur marchande
 * fitness for a particular pur-        ou de pertinence pour un usage
 * pose.  NRC shall not be liable       particulier.  Le CNRC ne
 * in any event for any damages,        pourra en aucun cas etre tenu
 * whether direct or indirect,          responsable de tout dommage,
 * special or general, consequen-       direct ou indirect, particul-
 * tial or incidental, arising          ier ou general, accessoire ou
 * from the use of the software.        fortuit, resultant de l'utili-
 *                                      sation du logiciel.
 *
 *
 * @author jenkinsd
 * 12/13/13 - 1:44 PM
 *
 *
 *
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 ************************************************************************
 */

package ca.nrc.cadc.caom2.ui.server;


import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


public class WalkthroughTest {

    private static final Logger LOGGER = Logger.getLogger(WalkthroughTest.class);

    static {
        LOGGER.setLevel(Level.DEBUG);
    }

    static URL getBaseURL() {
        try {
            final String fromEnv = System.getenv("CAOM2_UI_BASE_URL");
            LOGGER.debug("CAOM2_UI_BASE_URL: " + fromEnv);
            return new URL(Objects.requireNonNullElse(fromEnv, "http://localhost:8080/caom2ui"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Synchronized to prevent multiple tests from interfering with each other if run in parallel, since they may be
     * using the same underlying server and could cause issues with concurrent access to shared resources or state.
     * @param identifier    The ID query param
     * @return  Model of the first table.
     * @throws Exception    Any exception that occurs during the process of fetching and parsing the document.
     */
    private synchronized static DocumentModel goTo(final String identifier) throws Exception {
        Objects.requireNonNull(identifier, "identifier must not be null.");
        final URL baseURL = WalkthroughTest.getBaseURL();
        final String urlString = String.format("%s/%s?%s", baseURL.toExternalForm(), "view",
                                               String.format("ID=%s", URLEncoder.encode(identifier, StandardCharsets.UTF_8)));
        final URL endpoint = new URL(urlString);
        final Document document = Jsoup.parse(endpoint, 10000);
        final DocumentModel documentModel = DocumentModel.fromDocument(document);
        Assertions.assertEquals("CAOM Observation", documentModel.pageTitle, "Wrong title");

        return documentModel;
    }

    @Test
    public void observationViewTestIRIS() throws Exception {
        final DocumentModel document = WalkthroughTest.goTo("ivo://cadc.nrc.ca/IRIS?f008h000");
        final Map<String, String> tableValues = document.firstObservationTableValues;
        Assertions.assertEquals("IRIS", tableValues.get("collection"), "Wrong collection");
        Assertions.assertEquals("f008h000", tableValues.get("observationID"), "Wrong observation ID");
        Assertions.assertEquals("SimpleObservation", document.firstObservationTitle, "Wrong observation kind");
    }

    @Test
    public void observationViewTestCFHTMEGAPIPE() throws Exception {
        final DocumentModel document = WalkthroughTest.goTo("ivo://cadc.nrc.ca/CFHTMEGAPIPE?MegaPipe.189.210");
        final Map<String, String> tableValues = document.firstObservationTableValues;
        Assertions.assertEquals("CFHTMEGAPIPE", tableValues.get("collection"), "Wrong collection");
        Assertions.assertEquals("MegaPipe.189.210", tableValues.get("observationID"), "Wrong observation ID");
        Assertions.assertEquals("DerivedObservation", document.firstObservationTitle, "Wrong observation kind");
    }

    @Test
    public void observationViewTestHST() throws Exception {
        final DocumentModel document = WalkthroughTest.goTo("ivo://cadc.nrc.ca/mirror/HST?jbeoft020");
        final Map<String, String> tableValues = document.firstObservationTableValues;
        Assertions.assertEquals("HST", tableValues.get("collection"), "Wrong collection");
        Assertions.assertEquals("jbeoft020", tableValues.get("observationID"), "Wrong observation ID");
        Assertions.assertEquals("DerivedObservation", document.firstObservationTitle, "Wrong observation kind");
        Assertions.assertEquals("IMAGING", tableValues.get("type"), "Wrong type");
    }

    static final class DocumentModel {
        final String pageTitle;
        final String firstObservationTitle;
        final Map<String, String> firstObservationTableValues = new HashMap<>();

        DocumentModel(String title, String firstObservationTitle, Map<String, String> firstObservationTableValues) {
            this.pageTitle = title;
            this.firstObservationTitle = firstObservationTitle;
            this.firstObservationTableValues.putAll(
                    Objects.requireNonNullElse(firstObservationTableValues, Collections.emptyMap()));
        }

        static DocumentModel fromDocument(final Document document) {
            final String title = document.title();
            final String observationKind = DocumentModel.textValue(document, "body > div > div > h2");
            final Element firstObservationTableBody = document.selectFirst("body > div > div > table > tbody");
            if (firstObservationTableBody == null) {
                throw new IllegalStateException("No observation table body found in document.");
            }
            final Map<String, String> firstObservationTableValues = firstObservationTableBody.select("tr").stream().map(tableRow -> {
                final String key = Objects.requireNonNull(tableRow.selectFirst("td:nth-child(1)"), "Table cell key is null at row: " + tableRow.id()).text().trim();
                final Element valueElement = tableRow.selectFirst("td:nth-child(2)");
                final String value = valueElement == null ? "" : valueElement.text().trim();
                return Map.entry(key, value);
            }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            return new DocumentModel(title, observationKind, firstObservationTableValues);
        }

        static String textValue(final Document document, final String cssQuery) {
            final Element element = document.selectFirst(cssQuery);
            if (element == null) {
                throw new IllegalStateException(String.format("No element found for CSS query: %s", cssQuery));
            }
            return element.text();
        }
    }
}
