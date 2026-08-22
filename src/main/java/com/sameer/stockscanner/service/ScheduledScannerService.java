package com.sameer.stockscanner.service;

import com.sameer.stockscanner.model.ScanSummary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class ScheduledScannerService {

    private final StockNewsScannerService stockNewsScannerService;
    private final ScanEmailService scanEmailService;
    private final List<String> symbols;

    public ScheduledScannerService(
            StockNewsScannerService stockNewsScannerService,
            ScanEmailService scanEmailService,
            @Value("${scanner.symbols}") String configuredSymbols
    ) {
        this.stockNewsScannerService = stockNewsScannerService;
        this.scanEmailService = scanEmailService;
        this.symbols = Arrays.stream(configuredSymbols.split(","))
                .map(String::trim)
                .filter(symbol -> !symbol.isEmpty())
                .toList();
    }

    @Scheduled(cron = "${scanner.schedule.premarket-cron}", zone = "${scanner.schedule.zone}")
    public void runPremarketScan() {
        runScan();
    }

    @Scheduled(cron = "${scanner.schedule.midday-cron}", zone = "${scanner.schedule.zone}")
    public void runMiddayScan() {
        runScan();
    }

    @Scheduled(cron = "${scanner.schedule.after-close-cron}", zone = "${scanner.schedule.zone}")
    public void runAfterCloseScan() {
        runScan();
    }

    private void runScan() {
        ScanSummary summary = stockNewsScannerService.scan(symbols);
        scanEmailService.sendReport(summary);
    }
}
