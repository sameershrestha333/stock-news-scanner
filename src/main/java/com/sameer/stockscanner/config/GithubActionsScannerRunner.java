package com.sameer.stockscanner.config;

import com.sameer.stockscanner.service.ScheduledScannerService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("github-actions")
public class GithubActionsScannerRunner {

    @Bean
    CommandLineRunner runScannerOnce(ScheduledScannerService scheduledScannerService) {
        return args -> scheduledScannerService.scanAllSymbolsAndEmail();
    }
}
