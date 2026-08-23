package com.sameer.stockscanner.service;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("github-actions")
public class GitHubActionsScannerRunner implements ApplicationRunner {

    private final ScheduledScannerService scheduledScannerService;

    public GitHubActionsScannerRunner(ScheduledScannerService scheduledScannerService) {
        this.scheduledScannerService = scheduledScannerService;
    }

    @Override
    public void run(ApplicationArguments args) {
        scheduledScannerService.scanAllSymbolsAndEmail();
    }
}
