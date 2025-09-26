package com.automation.framework.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for handling test data from JSON files
 */
public class TestDataUtils {
    private static final Logger logger = LogManager.getLogger(TestDataUtils.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Read JSON test data file
     */
    public static JsonNode readJsonTestData(String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                logger.error("Test data file not found: {}", filePath);
                throw new RuntimeException("Test data file not found: " + filePath);
            }
            
            JsonNode jsonNode = objectMapper.readTree(file);
            logger.info("Successfully loaded test data from: {}", filePath);
            return jsonNode;
        } catch (IOException e) {
            logger.error("Failed to read test data file: {}", filePath, e);
            throw new RuntimeException("Failed to read test data file: " + filePath, e);
        }
    }

    /**
     * Get search queries from test data
     */
    public static List<SearchQuery> getSearchQueries(String filePath, String category) {
        JsonNode testData = readJsonTestData(filePath);
        JsonNode searchQueries = testData.path("searchQueries").path(category);
        
        List<SearchQuery> queries = new ArrayList<>();
        
        if (searchQueries.isArray()) {
            for (JsonNode queryNode : searchQueries) {
                SearchQuery query = new SearchQuery(
                    queryNode.path("query").asText(),
                    queryNode.path("expectedResultsContain").asText(),
                    queryNode.path("shouldPass").asBoolean(),
                    queryNode.path("description").asText()
                );
                queries.add(query);
            }
        }
        
        logger.info("Loaded {} search queries from category: {}", queries.size(), category);
        return queries;
    }

    /**
     * Get URL from test data
     */
    public static String getUrl(String filePath, String urlKey) {
        JsonNode testData = readJsonTestData(filePath);
        String url = testData.path("urls").path(urlKey).asText();
        
        if (url.isEmpty()) {
            logger.warn("URL not found for key: {}", urlKey);
        }
        
        return url;
    }

    /**
     * Get timeout value from test data
     */
    public static int getTimeout(String filePath, String timeoutKey) {
        JsonNode testData = readJsonTestData(filePath);
        return testData.path("timeouts").path(timeoutKey).asInt(30); // Default 30 seconds
    }

    /**
     * Get browser list from test data
     */
    public static List<String> getBrowsers(String filePath) {
        JsonNode testData = readJsonTestData(filePath);
        JsonNode browsers = testData.path("browsers");
        
        List<String> browserList = new ArrayList<>();
        
        if (browsers.isArray()) {
            for (JsonNode browser : browsers) {
                browserList.add(browser.asText());
            }
        }
        
        return browserList;
    }

    /**
     * SearchQuery data class
     */
    public static class SearchQuery {
        private final String query;
        private final String expectedResultsContain;
        private final boolean shouldPass;
        private final String description;

        public SearchQuery(String query, String expectedResultsContain, boolean shouldPass, String description) {
            this.query = query;
            this.expectedResultsContain = expectedResultsContain;
            this.shouldPass = shouldPass;
            this.description = description;
        }

        public String getQuery() {
            return query;
        }

        public String getExpectedResultsContain() {
            return expectedResultsContain;
        }

        public boolean shouldPass() {
            return shouldPass;
        }

        public String getDescription() {
            return description;
        }

        @Override
        public String toString() {
            return String.format("SearchQuery{query='%s', description='%s'}", query, description);
        }
    }
}