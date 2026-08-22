package com.sameer.stockscanner.service;

import com.sameer.stockscanner.model.ScanSummary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
public class ScanEmailService {

    private static final ZoneId CENTRAL_TIME = ZoneId.of("America/Chicago");
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter
            .ofPattern("yyyy-MM-dd HH:mm:ss z")
            .withZone(CENTRAL_TIME);

    private final JavaMailSender mailSender;
    private final String from;
    private final String recipient;

    public ScanEmailService(
            JavaMailSender mailSender,
            @Value("${spring.mail.username}") String from,
            @Value("${scanner.email.to:${spring.mail.username}}") String recipient
    ) {
        this.mailSender = mailSender;
        this.from = from;
        this.recipient = recipient;
    }

    public void sendReport(ScanSummary summary) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(recipient);
        message.setSubject("Stock News Scanner Report - " + TIMESTAMP_FORMAT.format(summary.generatedAt()));
        message.setText(buildReport(summary));
        mailSender.send(message);
    }

    private String buildReport(ScanSummary summary) {
        StringBuilder report = new StringBuilder();
        report.append("Stock News Scanner Report\n");
        report.append("Generated: ").append(TIMESTAMP_FORMAT.format(summary.generatedAt())).append('\n');
        report.append("Symbols scanned: ").append(summary.scannedSymbols()).append('\n');
        report.append("Stocks with important news: ").append(summary.stocksWithImportantNews()).append("\n\n");

        if (summary.stockNews().isEmpty()) {
            report.append("No important news found.");
            return report.toString();
        }

        report.append("Important news:\n");
        for (Map.Entry<String, String> entry : summary.stockNews().entrySet()) {
            report.append(entry.getKey()).append(": ").append(entry.getValue()).append('\n');
        }
        return report.toString();
    }
}
