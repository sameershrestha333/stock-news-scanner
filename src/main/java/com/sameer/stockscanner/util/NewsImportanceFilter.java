package com.sameer.stockscanner.util;

import com.sameer.stockscanner.model.NewsArticle;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Component
public class NewsImportanceFilter {

    public enum Direction {
        BULLISH,
        BEARISH,
        WATCH
    }

    private static final Map<String, Integer> KEYWORD_SCORES = new LinkedHashMap<>();

    private static final List<String> BULLISH_KEYWORDS = List.of(
            "upgraded to buy",
            "upgraded to overweight",
            "upgraded to outperform",
            "initiated with a buy",
            "initiated with an overweight",
            "initiated with an outperform",
            "raises price target",
            "raised price target",
            "price target raised",
            "raises guidance",
            "guidance raised",
            "raises outlook",
            "reaffirms guidance",
            "beats estimates",
            "beats expectations",
            "dividend increase",
            "share repurchase",
            "buyback",
            "fda approval",
            "fda clearance",
            "positive clinical trial"
    );

    private static final List<String> BEARISH_KEYWORDS = List.of(
            "downgraded to sell",
            "downgraded to underweight",
            "downgraded to underperform",
            "initiated with a sell",
            "initiated with an underweight",
            "initiated with an underperform",
            "lowers price target",
            "lowered price target",
            "price target lowered",
            "cuts guidance",
            "guidance lowered",
            "cuts outlook",
            "misses estimates",
            "misses expectations",
            "bankruptcy",
            "sec investigation",
            "data breach",
            "cyberattack",
            "restatement",
            "delisting",
            "recall",
            "layoffs",
            "restructuring",
            "resigns"
    );

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
        KEYWORD_SCORES.put("restatement", 4);
        KEYWORD_SCORES.put("delisting", 4);

        KEYWORD_SCORES.put("upgraded to buy", 3);
        KEYWORD_SCORES.put("upgraded to overweight", 3);
        KEYWORD_SCORES.put("upgraded to outperform", 3);
        KEYWORD_SCORES.put("downgraded to sell", 3);
        KEYWORD_SCORES.put("downgraded to underweight", 3);
        KEYWORD_SCORES.put("downgraded to underperform", 3);
        KEYWORD_SCORES.put("initiated with a buy", 3);
        KEYWORD_SCORES.put("initiated with an overweight", 3);
        KEYWORD_SCORES.put("initiated with an outperform", 3);
        KEYWORD_SCORES.put("initiated with a sell", 3);
        KEYWORD_SCORES.put("initiated with an underweight", 3);
        KEYWORD_SCORES.put("initiated with an underperform", 3);
        KEYWORD_SCORES.put("raises guidance", 3);
        KEYWORD_SCORES.put("cuts guidance", 3);
        KEYWORD_SCORES.put("guidance raised", 3);
        KEYWORD_SCORES.put("guidance lowered", 3);
        KEYWORD_SCORES.put("raises outlook", 3);
        KEYWORD_SCORES.put("cuts outlook", 3);
        KEYWORD_SCORES.put("reaffirms guidance", 3);
        KEYWORD_SCORES.put("earnings", 3);
        KEYWORD_SCORES.put("quarterly results", 3);
        KEYWORD_SCORES.put("revenue growth", 3);
        KEYWORD_SCORES.put("revenue forecast", 3);
        KEYWORD_SCORES.put("beats estimates", 3);
        KEYWORD_SCORES.put("beats expectations", 3);
        KEYWORD_SCORES.put("misses estimates", 3);
        KEYWORD_SCORES.put("misses expectations", 3);
        KEYWORD_SCORES.put("dividend", 3);
        KEYWORD_SCORES.put("share repurchase", 3);
        KEYWORD_SCORES.put("buyback", 3);
        KEYWORD_SCORES.put("layoffs", 3);
        KEYWORD_SCORES.put("restructuring", 3);
        KEYWORD_SCORES.put("recall", 3);

        KEYWORD_SCORES.put("raises price target", 2);
        KEYWORD_SCORES.put("raised price target", 2);
        KEYWORD_SCORES.put("lowers price target", 2);
        KEYWORD_SCORES.put("lowered price target", 2);
        KEYWORD_SCORES.put("price target raised", 2);
        KEYWORD_SCORES.put("price target lowered", 2);
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

    public Direction direction(NewsArticle article) {
        if (article == null || article.title() == null) {
            return Direction.WATCH;
        }

        String title = article.title().toLowerCase(Locale.US);
        if (containsAny(title, BEARISH_KEYWORDS)) {
            return Direction.BEARISH;
        }
        if (containsAny(title, BULLISH_KEYWORDS)) {
            return Direction.BULLISH;
        }
        return Direction.WATCH;
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

    private boolean containsAny(String title, List<String> keywords) {
        return keywords.stream().anyMatch(title::contains);
    }
}
