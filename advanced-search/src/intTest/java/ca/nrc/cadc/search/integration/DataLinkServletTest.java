package ca.nrc.cadc.search.integration;

import ca.nrc.cadc.dali.tables.votable.VOTableDocument;
import ca.nrc.cadc.dali.tables.votable.VOTableField;
import ca.nrc.cadc.dali.tables.votable.VOTableReader;
import ca.nrc.cadc.dali.tables.votable.VOTableResource;
import ca.nrc.cadc.dali.tables.votable.VOTableTable;
import ca.nrc.cadc.io.ResourceIterator;
import ca.nrc.cadc.net.HttpGet;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DataLinkServletTest {
    private static final Logger LOGGER = Logger.getLogger(DataLinkServletTest.class);
    private static final String ENDPOINT = "datalink";


    static {
        LOGGER.setLevel(Level.DEBUG);
    }

    /**
     * Assert that the output of the datalink servlet at the given path contains a results table with the expected values for the given fields.
     * Requires a LinkedHashMap to preserve the order of the fields, which is important for the test.
     * @param query  The path to the datalink servlet endpoint, e.g. "ID=ivo://cadc.nrc.ca/data/caom2/obsid/12345"
     * @throws Exception   Any exception thrown during the test will be propagated, causing the test to fail. This includes assertion failures and any exceptions thrown while making the HTTP request or parsing the VOTable response.
     */
    private static VOTableTable getResultsTableOutput(final String query) throws Exception {
        final URL url = new URL(TestConfig.getBaseURL(), String.format("%s?%s", DataLinkServletTest.ENDPOINT, query));
        LOGGER.debug("URL: " + url);
        final HttpGet httpGet = new HttpGet(url, true);
        httpGet.run();

        try (final InputStream inputStream = httpGet.getInputStream()) {
            final VOTableReader votableReader = new VOTableReader();
            final VOTableDocument document = votableReader.read(inputStream);
            final VOTableResource resultsResource = document.getResourceByType("results");
            return resultsResource.getTable();
        }
    }

    private static List<List<Object>> getRowsBy(final TableField field, final Object value,
                                                final VOTableTable resultsTable) throws IOException {
        final List<VOTableField> fields = resultsTable.getFields();
        final List<List<Object>> matchingRows = new ArrayList<>();
        final int fieldIndex = DataLinkServletTest.getFieldIndex(fields, field);

        try (final ResourceIterator<List<Object>> rowIterator = resultsTable.getTableData().iterator()) {
            while (rowIterator.hasNext()) {
                final List<Object> row = rowIterator.next();
                if (row.get(fieldIndex).equals(value)) {
                    matchingRows.add(row);
                }
            }
        }
        return matchingRows;
    }

    private static int getFieldIndex(final List<VOTableField> fields, TableField field) {
        for (int i = 0; i < fields.size(); i++) {
            if (fields.get(i).getName().equals(field.fieldName)) {
                return i;
            }
        }
        throw new IllegalArgumentException("Field not found: " + field.fieldName);
    }

    @Test
    public void testBLAST() throws Exception {
        final VOTableTable resultsTable = DataLinkServletTest.getResultsTableOutput(
                String.format("%s=%s", "id", URLEncoder.encode("ivo://cadc.nrc.ca/BLAST?BLASTbullet2006-12-21/SMOOTH_350_2008-11-11",
                        StandardCharsets.UTF_8)));
        final List<VOTableField> fields = resultsTable.getFields();
        final List<List<Object>> matchingRows = DataLinkServletTest.getRowsBy(TableField.SEMANTICS, "#cutout", resultsTable);
        Assertions.assertFalse(matchingRows.isEmpty(), "Should have at least one row with semantics #cutout");
        matchingRows.forEach(matchingRow -> {
            Assertions.assertEquals("ivo://cadc.nrc.ca/BLAST?BLASTbullet2006-12-21/SMOOTH_350_2008-11-11",
                    matchingRow.get(DataLinkServletTest.getFieldIndex(fields, TableField.ID)));
            Assertions.assertEquals("application/fits", matchingRow.get(DataLinkServletTest.getFieldIndex(fields, TableField.CONTENT_TYPE)));
            Assertions.assertNull(matchingRow.get(DataLinkServletTest.getFieldIndex(fields, TableField.ACCESS_URL)));
        });
    }

    @Test
    public void testIRIS() throws Exception {
        final VOTableTable resultsTable = DataLinkServletTest.getResultsTableOutput(
                String.format("%s=%s", "id", URLEncoder.encode("ivo://cadc.nrc.ca/IRIS?f091h000/IRAS-100um", StandardCharsets.UTF_8)));
        final List<VOTableField> fields = resultsTable.getFields();
        final List<List<Object>> matchingRows = DataLinkServletTest.getRowsBy(TableField.SEMANTICS, "#preview", resultsTable);
        Assertions.assertFalse(matchingRows.isEmpty(), "Should have at least one row with semantics #preview");
        matchingRows.forEach(matchingRow -> {
            Assertions.assertEquals("ivo://cadc.nrc.ca/IRIS?f091h000/IRAS-100um",
                    matchingRow.get(DataLinkServletTest.getFieldIndex(fields, TableField.ID)));
            Assertions.assertEquals("image/png", matchingRow.get(DataLinkServletTest.getFieldIndex(fields, TableField.CONTENT_TYPE)));
            Assertions.assertEquals("https://ws.cadc-ccda.hia-iha.nrc-cnrc.gc.ca/raven/files/cadc:IRIS/f091h000_preview_1024.png", matchingRow.get(DataLinkServletTest.getFieldIndex(fields, TableField.ACCESS_URL)));
        });
    }

    enum TableField {
        ID("ID"),
        SEMANTICS("semantics"),
        ACCESS_URL("access_url"),
        CONTENT_TYPE("content_type");

        private final String fieldName;

        TableField(String fieldName) {
            this.fieldName = fieldName;
        }

        public String getFieldName() {
            return fieldName;
        }
    }
}
