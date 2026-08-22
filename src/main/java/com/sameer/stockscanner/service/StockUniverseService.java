package com.sameer.stockscanner.service;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class StockUniverseService {

    public List<String> getAllSymbols() {
        return Arrays.asList(StockSectors.getAllSymbols());
    }

    public List<String> getSymbolsForSector(String sector) {
        if (sector == null || sector.isBlank()) {
            return List.of();
        }

        Map<String, String[]> sectors = StockSectors.getSectors();
        String wanted = sector.trim().toLowerCase(Locale.US);

        return sectors.entrySet().stream()
                .filter(entry -> entry.getKey().toLowerCase(Locale.US).equals(wanted))
                .findFirst()
                .map(entry -> Arrays.asList(entry.getValue()))
                .orElse(List.of());
    }
}
