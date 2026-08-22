package com.sameer.stockscanner.service;

import com.sameer.stockscanner.model.NewsArticle;
import com.sameer.stockscanner.model.ScanSummary;
import com.sameer.stockscanner.util.NewsImportanceFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StockNewsScannerService {

    private final GoogleNewsService googleNewsService;
    private final NewsImportanceFilter importanceFilter;
    private final int minimumImportanceScore;
    private final int maximumHeadlinesPerStock;
    private final long delayBetweenSymbolsMs;

    public StockNewsScannerService(
            GoogleNewsService googleNewsService,
            NewsImportanceFilter importanceFilter,
            @Value("${scanner.minimum-importance-score:2}") int minimumImportanceScore,
            @Value("${scanner.maximum-headlines-per-stock:3}") int maximumHeadlinesPerStock,
            @Value("${scanner.delay-between-symbols-ms:400}") long delayBetweenSymbolsMs
    ) {
        this.googleNewsService = googleNewsService;
        this.importanceFilter = importanceFilter;
        this.minimumImportanceScore = minimumImportanceScore;
        this.maximumHeadlinesPerStock = maximumHeadlinesPerStock;
        this.delayBetweenSymbolsMs = delayBetweenSymbolsMs;
    }

    public ScanSummary scan(List<String> symbols) {
        Map<String, String> stockNews = new LinkedHashMap<>();
        List<String> normalized = normalize(symbols);

        for (String symbol : normalized) {
            List<NewsArticle> importantArticles = googleNewsService.findRecentNews(symbol).stream()
                    .filter(article -> importanceFilter.isImportant(article, minimumImportanceScore))
                    .limit(maximumHeadlinesPerStock)
                    .toList();

            if (!importantArticles.isEmpty()) {
                stockNews.put(symbol, toSummary(importantArticles));
            }
            pause();
        }

        return new ScanSummary(stockNews, normalized.size(), stockNews.size(), Instant.now());
    }

    private List<String> normalize(List<String> symbols) {
        if (symbols == null) {
            return List.of();
        }
        return symbols.stream()
                .filter(symbol -> symbol != null && !symbol.isBlank())
                .map(symbol -> symbol.trim().toUpperCase())
                .distinct()
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private String toSummary(List<NewsArticle> articles) {
        int highestScore = articles.stream().mapToInt(importanceFilter::importanceScore).max().orElse(0);
        NewsImportanceFilter.Direction overallDirection = articles.stream()
                .map(importanceFilter::direction)
                .min(this::compareDirectionPriority)
                .orElse(NewsImportanceFilter.Direction.WATCH);
        String headlines = articles.stream()
                .map(article -> "[" + article.source() + "] " + article.title())
                .collect(Collectors.joining(" | "));
        return overallDirection + " | Score: " + highestScore + " | " + headlines;
    }

    private int compareDirectionPriority(
            NewsImportanceFilter.Direction first,
            NewsImportanceFilter.Direction second
    ) {
        return Integer.compare(directionPriority(first), directionPriority(second));
    }

    private int directionPriority(NewsImportanceFilter.Direction direction) {
        return switch (direction) {
            case BEARISH -> 0;
            case BULLISH -> 1;
            case WATCH -> 2;
        };
    }

    private void pause() {
        try {
            Thread.sleep(delayBetweenSymbolsMs);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        }
    }
}
