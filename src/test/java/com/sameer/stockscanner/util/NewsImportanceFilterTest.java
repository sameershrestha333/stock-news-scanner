package com.sameer.stockscanner.util;

import com.sameer.stockscanner.model.NewsArticle;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NewsImportanceFilterTest {

    private final NewsImportanceFilter filter = new NewsImportanceFilter();

    @Test
    void marksEarningsAndGuidanceHeadlineAsImportant() {
        NewsArticle article = new NewsArticle("Company beats earnings estimates and raises guidance", "Example", "https://example.com", Instant.now());
        assertTrue(filter.isImportant(article, 2));
    }

    @Test
    void ignoresGenericMarketHeadline() {
        NewsArticle article = new NewsArticle("Stocks move higher in afternoon trading", "Example", "https://example.com", Instant.now());
        assertFalse(filter.isImportant(article, 2));
    }
}
