package com.sameer.stockscanner.model;

import java.time.Instant;
import java.util.Map;

public record ScanSummary(
        Map<String, String> stockNews,
        int scannedSymbols,
        int stocksWithImportantNews,
        Instant generatedAt
) {
}
