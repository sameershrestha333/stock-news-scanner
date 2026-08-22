package com.sameer.stockscanner.util;

import com.sameer.stockscanner.model.NewsArticle;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;

@Component
public class NewsImportanceFilter {

    private static final List<String> HIGH_IMPACT_TERMS = List.of(
            "earnings", "revenue", "profit", "guidance", "forecast",
            "beats estimates", "misses estimates", "raises outlook", "cuts outlook",
            "upgrade", "upgraded", "downgrade", "downgraded", "price target",
            "merger", "acquisition", "acquire", "takeover",
            "fda approval", "fda", "recall", "bankruptcy",
            "lawsuit", "investigation", "probe", "antitrust", "sec",
            "dividend", "buyback", "share repurchase", "ceo resigns", "ceo steps down"
    );

    public int importanceScore(NewsArticle article) {
        String text = (article.title() + " " + article.source()).toLowerCase(Locale.US);
        return (int) HIGH_IMPACT_TERMS.stream().filter(text::contains).count();
    }

    public boolean isImportant(NewsArticle article, int minimumScore) {
        return importanceScore(article) >= minimumScore;
    }
}
