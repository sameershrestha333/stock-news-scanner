package com.sameer.stockscanner.service;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.sameer.stockscanner.model.NewsArticle;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.StringReader;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class GoogleNewsService {

    private static final Pattern TICKER_TOKEN_PATTERN = Pattern.compile("(?<![A-Z0-9])\\$?[A-Z]{1,5}(?![A-Z0-9])");
    private static final int MAX_TICKERS_IN_HEADLINE = 3;

    private final HttpClient httpClient;
    private final Duration lookback;

    public GoogleNewsService(@Value("${scanner.lookback-hours:24}") long lookbackHours) {
        this.httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(15)).build();
        this.lookback = Duration.ofHours(lookbackHours);
    }

    public List<NewsArticle> findRecentNews(String symbol) {
        try {
            String normalizedSymbol = symbol.trim().toUpperCase();
            String query = URLEncoder.encode(normalizedSymbol + " stock when:1d", StandardCharsets.UTF_8);
            String rssUrl = "https://news.google.com/rss/search?q=" + query + "&hl=en-US&gl=US&ceid=US:en";
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(rssUrl))
                    .header("User-Agent", "StockNewsScanner/1.0")
                    .timeout(Duration.ofSeconds(20))
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200 ? parse(response.body(), normalizedSymbol) : List.of();
        } catch (Exception ignored) {
            return List.of();
        }
    }

    private List<NewsArticle> parse(String xml, String symbol) throws Exception {
        SyndFeed feed = new SyndFeedInput().build(new StringReader(xml));
        Instant cutoff = Instant.now().minus(lookback);
        List<NewsArticle> articles = new ArrayList<>();

        for (SyndEntry entry : feed.getEntries()) {
            if (entry.getPublishedDate() == null) {
                continue;
            }

            String title = entry.getTitle();
            Instant publishedAt = entry.getPublishedDate().toInstant();
            if (publishedAt.isBefore(cutoff)
                    || !mentionsSymbol(title, symbol)
                    || isBroadMarketRoundup(title)) {
                continue;
            }

            articles.add(new NewsArticle(
                    cleanTitle(title),
                    sourceFromTitle(title),
                    entry.getLink(),
                    publishedAt
            ));
        }
        return articles;
    }

    private boolean mentionsSymbol(String title, String symbol) {
        if (title == null || title.isBlank()) {
            return false;
        }

        if (symbol.length() == 1) {
            String strictTickerPattern = "(?i)(?:\\$" + Pattern.quote(symbol) + "(?![A-Z0-9])|\\(" + Pattern.quote(symbol) + "\\))";
            return Pattern.compile(strictTickerPattern).matcher(title).find();
        }

        String pattern = "(?i)(?<![A-Z0-9])\\$?" + Pattern.quote(symbol) + "(?![A-Z0-9])";
        return Pattern.compile(pattern).matcher(title).find();
    }

    private boolean isBroadMarketRoundup(String title) {
        if (title == null || title.isBlank()) {
            return true;
        }

        Matcher matcher = TICKER_TOKEN_PATTERN.matcher(title.toUpperCase());
        int tickerCount = 0;
        while (matcher.find()) {
            tickerCount++;
            if (tickerCount > MAX_TICKERS_IN_HEADLINE) {
                return true;
            }
        }
        return false;
    }

    private String cleanTitle(String title) {
        int separator = title.lastIndexOf(" - ");
        return separator > 0 ? title.substring(0, separator).trim() : title;
    }

    private String sourceFromTitle(String title) {
        int separator = title.lastIndexOf(" - ");
        return separator > 0 ? title.substring(separator + 3).trim() : "Unknown";
    }
}
