package com.sameer.stockscanner.model;

import java.time.Instant;

public record NewsArticle(String title, String source, String url, Instant publishedAt) {
}
