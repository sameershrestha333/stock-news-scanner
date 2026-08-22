package com.sameer.stockscanner.controller;

import com.sameer.stockscanner.dto.ScanRequest;
import com.sameer.stockscanner.model.ScanSummary;
import com.sameer.stockscanner.service.StockNewsScannerService;
import com.sameer.stockscanner.service.StockUniverseService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/stocks")
public class StockNewsController {

    private final StockNewsScannerService scannerService;
    private final StockUniverseService universeService;

    public StockNewsController(StockNewsScannerService scannerService, StockUniverseService universeService) {
        this.scannerService = scannerService;
        this.universeService = universeService;
    }

    @PostMapping("/scan")
    public ResponseEntity<ScanSummary> scanCustomSymbols(@Valid @RequestBody ScanRequest request) {
        return ResponseEntity.ok(scannerService.scan(request.symbols()));
    }

    @PostMapping("/scan/all")
    public ResponseEntity<ScanSummary> scanAllSymbols() {
        return ResponseEntity.ok(scannerService.scan(universeService.getAllSymbols()));
    }

    @PostMapping("/scan/sector/{sector}")
    public ResponseEntity<ScanSummary> scanSector(@PathVariable String sector) {
        List<String> symbols = universeService.getSymbolsForSector(sector);
        return symbols.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(scannerService.scan(symbols));
    }
}
