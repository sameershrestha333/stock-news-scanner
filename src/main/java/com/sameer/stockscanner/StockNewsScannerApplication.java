package com.sameer.stockscanner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class StockNewsScannerApplication {

    public static void main(String[] args) {
        SpringApplication.run(StockNewsScannerApplication.class, args);
    }
}
