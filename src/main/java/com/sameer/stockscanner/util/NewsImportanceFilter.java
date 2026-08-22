package com.sameer.stockscanner.util;

import com.sameer.stockscanner.model.NewsArticle;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

@Component
public class NewsImportanceFilter {

    private static final Map<String, Integer> KEYWORD_SCORES = new LinkedHashMap<>();

    static {
        KEYWORD_SCORES.put("bankruptcy", 4);
        KEYWORD_SCORES.put("merger", 4);
        KEYWORD_SCORES.put("acquisition", 4);
        KEYWORD_SCORES.put("takeover", 4);
        KEYWORD_SCORES.put("fda approval", 4);
        KEYWORD_SCORES.put("clinical trial", 4);
        KEYWORD_SCORES.put("sec investigation", 4);
        KEYWORD_SCORES.put("data breach", 4);
        KEYWORD_SCORES.put("cyberattack", 4);

        KEYWORD_SCORES.put("raises guidance", 3);
        KEYWORD_SCORES.put("cuts guidance", 3);
        KEYWORD_SCORES.put("raises outlook", 3);
        KEYWORD_SCORES.put("cuts outlook", 3);
        KEYWORD_SCORES.put("earnings", 3);
        KEYWORD_SCORES.put("revenue growth", 3);
        KEYWORD_SCORES.put("revenue forecast", 3);
        KEYWORD_SCORES.put("beats estimates", 3);
        KEYWORD_SCORES.put("misses estimates", 3);
        KEYWORD_SCORES.put("dividend", 3);
        KEYWORD_SCORES.put("share repurchase", 3);
        KEYWORD_SCORES.put("buyback", 3);
        KEYWORD_SCORES.put("layoffs", 3);
        KEYWORD_SCORES.put("restructuring", 3);
        KEYWORD_SCORES.put("recall", 3);

        KEYWORD_SCORES.put("upgraded", 2);
        KEYWORD_SCORES.put("downgraded", 2);
        KEYWORD_SCORES.put("price target", 2);
        KEYWORD_SCORES.put("forecast raised", 2);
        KEYWORD_SCORES.put("forecast cut", 2);
        KEYWORD_SCORES.put("partnership", 2);
        KEYWORD_SCORES.put("contract", 2);
        KEYWORD_SCORES.put("appoints", 2);
        KEYWORD_SCORES.put("resigns", 2);
        KEYWORD_SCORES.put("stock split", 2);
    }

    public boolean isImportant(NewsArticle article, int minimumScore) {
        return importanceScore(article) >= minimumScore;
    }

    public int importanceScore(NewsArticle article) {
        if (article == null || article.title() == null) {
            return 0;
        }

        String title = article.title().toLowerCase(Locale.US);

        return KEYWORD_SCORES.entrySet().stream()
                .filter(entry -> title.contains(entry.getKey()))
                .mapToInt(Map.Entry::getValue)
                .max()
                .orElse(0);
    }

    public String getMatchedKeyword(NewsArticle article) {
        if (article == null || article.title() == null) {
            return "";
        }

        String title = article.title().toLowerCase(Locale.US);

        return KEYWORD_SCORES.keySet().stream()
                .filter(title::contains)
                .findFirst()
                .orElse("");
    }
}
