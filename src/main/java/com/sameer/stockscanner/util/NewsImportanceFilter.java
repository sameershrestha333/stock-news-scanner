package com.sameer.stockscanner.util;

import com.sameer.stockscanner.model.NewsArticle;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Locale;

@Component
public class NewsImportanceFilter {

    private static final String[] FILTERED_KEYS = {
            "earnings",
            "raises guidance",
            "cuts guidance",
            "upgraded",
            "downgraded",
            "price target",
            "acquisition",
            "merger",
            "fda approval",
            "stock split"
    };

    public boolean isImportant(NewsArticle article, int minimumScore) {
        return !getMatchedKeyword(article).isBlank();
    }

    public int importanceScore(NewsArticle article) {
        return getMatchedKeyword(article).isBlank() ? 0 : 1;
    }

    public String getMatchedKeyword(NewsArticle article) {
        if (article == null || article.title() == null) {
            return "";
        }

        String title = article.title().toLowerCase(Locale.US);

        return Arrays.stream(FILTERED_KEYS)
                .filter(title::contains)
                .findFirst()
                .orElse("");
    }
}
