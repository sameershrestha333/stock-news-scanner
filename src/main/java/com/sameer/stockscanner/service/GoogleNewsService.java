package com.sameer.stockscanner.service;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
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

@Service
public class GoogleNewsService {

    private final HttpClient httpClient;
    private final Duration lookback;

    public GoogleNewsService(@Value("${scanner.lookback-hours:24}") long lookbackHours) {
        this.httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(15)).build();
        this.lookback = Duration.ofHours(lookbackHours);
    }

    public List<NewsArticle> findRecentNews(String symbol) {
        try {
            String query = URLEncoder.encode(symbol.trim().toUpperCase() + " stock when:1d", StandardCharsets.UTF_8);
            String rssUrl = "https://news.google.com/rss/search?q=" + query + "&hl=en-US&gl=US&ceid=US:en";
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(rssUrl))
                    .header("User-Agent", "StockNewsScanner/1.0")
                    .timeout(Duration.ofSeconds(20))
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200 ? parse(response.body()) : List.of();
        } catch (Exception ignored) {
            return List.of();
        }
    }

    private List<NewsArticle> parse(String xml) throws Exception {
        SyndFeed feed = new SyndFeedInput().build(new XmlReader(new StringReader(xml)));
        Instant cutoff = Instant.now().minus(lookback);
        List<NewsArticle> articles = new ArrayList<>();

        for (SyndEntry entry : feed.getEntries()) {
            Instant publishedAt = entry.getPublishedDate() == null ? Instant.now() : entry.getPublishedDate().toInstant();
            if (publishedAt.isBefore(cutoff)) {
                continue;
            }
            articles.add(new NewsArticle(cleanTitle(entry.getTitle()), sourceFromTitle(entry.getTitle()), entry.getLink(), publishedAt));
        }
        return articles;
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
