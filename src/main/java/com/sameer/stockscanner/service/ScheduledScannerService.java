package com.sameer.stockscanner.service;

import com.sameer.stockscanner.model.ScanSummary;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScheduledScannerService {

    private final StockNewsScannerService scannerService;
    private final StockUniverseService universeService;
    private final ScanEmailService scanEmailService;

    public ScheduledScannerService(
            StockNewsScannerService scannerService,
            StockUniverseService universeService,
            ScanEmailService scanEmailService
    ) {
        this.scannerService = scannerService;
        this.universeService = universeService;
        this.scanEmailService = scanEmailService;
    }

    @Scheduled(cron = "${scanner.schedule.premarket-cron}", zone = "${scanner.schedule.zone}")
    public void runPremarketScan() {
        scanAllSymbolsAndEmail();
    }

    @Scheduled(cron = "${scanner.schedule.midday-cron}", zone = "${scanner.schedule.zone}")
    public void runMiddayScan() {
        scanAllSymbolsAndEmail();
    }

    @Scheduled(cron = "${scanner.schedule.after-close-cron}", zone = "${scanner.schedule.zone}")
    public void runAfterCloseScan() {
        scanAllSymbolsAndEmail();
    }

    public ScanSummary scanAllSymbolsAndEmail() {
        List<String> symbols = universeService.getAllSymbols();
        ScanSummary summary = scannerService.scan(symbols);
        scanEmailService.sendReport(summary);
        return summary;
    }
}
