package com.sameer.stockscanner.service;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public final class StockSectors {

    private StockSectors() {
    }

    public static String[] getTechnologySymbols() {
        return new String[] {"NVDA", "MSFT", "AAPL", "AVGO", "PLTR", "ORCL", "AMD", "CRM", "MU", "NOW", "CSCO", "IBM", "INTC", "QCOM", "TXN", "AMAT", "LRCX", "KLAC", "ANET", "APP"};
    }

    public static String[] getFinanceSymbols() {
        return new String[] {"JPM", "BRK.B", "V", "MA", "BAC", "WFC", "GS", "MS", "C", "AXP", "BLK", "SCHW", "SPGI", "COF", "PGR"};
    }

    public static String[] getConsumerGoodsSymbols() {
        return new String[] {"AMZN", "WMT", "COST", "TSLA", "HD", "MCD", "NKE", "LOW", "TJX", "SBUX", "BKNG", "ORLY", "MAR", "GM", "F", "TGT", "ROST", "CMG", "DHI", "AZO"};
    }

    public static String[] getMediaSymbols() {
        return new String[] {"META", "GOOGL", "NFLX", "DIS", "WBD", "FOXA", "SPOT", "ROKU", "LYV", "EA"};
    }

    public static String[] getHealthcareSymbols() {
        return new String[] {"LLY", "JNJ", "ABBV", "UNH", "MRK", "ABT", "TMO", "ISRG", "PFE", "DHR", "AMGN", "GILD", "VRTX", "REGN", "CVS"};
    }

    public static String[] getTelecomSymbols() {
        return new String[] {"VZ", "TMUS", "T", "CMCSA", "CHTR", "VOD", "AMX", "ORAN", "TU", "LBTYA"};
    }

    public static String[] getEnergySymbols() {
        return new String[] {"XOM", "CVX", "COP", "SHEL", "EOG", "WMB", "SLB", "MPC", "PSX", "OKE", "OXY", "VLO", "KMI", "HAL"};
    }

    public static String[] getIndustrialsSymbols() {
        return new String[] {"CAT", "GE", "BA", "UPS", "FDX", "HON", "UNP", "RTX", "LMT", "DE", "ETN", "NOC", "GD", "WM", "CSX"};
    }

    public static String[] getMaterialsSymbols() {
        return new String[] {"LIN", "APD", "FCX", "NEM", "SHW", "ECL", "NUE", "DOW", "PPG", "VMC"};
    }

    public static String[] getRealEstateSymbols() {
        return new String[] {"PLD", "AMT", "EQIX", "O", "WELL", "SPG", "PSA", "DLR", "CCI", "VICI"};
    }

    public static String[] getUtilitiesSymbols() {
        return new String[] {"NEE", "DUK", "SO", "AEP", "D", "EXC", "XEL", "ED", "PEG", "SRE"};
    }

    public static Map<String, String[]> getSectors() {
        Map<String, String[]> sectors = new LinkedHashMap<>();
        sectors.put("technology", getTechnologySymbols());
        sectors.put("finance", getFinanceSymbols());
        sectors.put("consumerGoods", getConsumerGoodsSymbols());
        sectors.put("media", getMediaSymbols());
        sectors.put("healthcare", getHealthcareSymbols());
        sectors.put("telecom", getTelecomSymbols());
        sectors.put("energy", getEnergySymbols());
        sectors.put("industrials", getIndustrialsSymbols());
        sectors.put("materials", getMaterialsSymbols());
        sectors.put("realEstate", getRealEstateSymbols());
        sectors.put("utilities", getUtilitiesSymbols());
        return sectors;
    }

    public static String[] getAllSymbols() {
        Set<String> symbols = new LinkedHashSet<>();
        getSectors().values().forEach(items -> symbols.addAll(Arrays.asList(items)));
        return symbols.toArray(String[]::new);
    }
}
