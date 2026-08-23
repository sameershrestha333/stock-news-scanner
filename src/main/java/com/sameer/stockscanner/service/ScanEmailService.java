package com.sameer.stockscanner.service;

import com.sameer.stockscanner.model.ScanSummary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class ScanEmailService {

    private static final ZoneId CENTRAL_TIME = ZoneId.of("America/Chicago");
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter
            .ofPattern("MMM d, uuuu • h:mm a z", Locale.US)
            .withZone(CENTRAL_TIME);
    private static final String DIVIDER = "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━";

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
        message.setSubject("📈 Stock News Scanner: "
                + summary.stocksWithImportantNews()
                + " important alert(s)");
        message.setText(buildReport(summary));
        mailSender.send(message);
    }

    private String buildReport(ScanSummary summary) {
        List<Alert> alerts = summary.stockNews().entrySet().stream()
                .map(entry -> toAlert(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparingInt(Alert::score)
                        .reversed()
                        .thenComparing(Alert::ticker))
                .toList();

        long bullishCount = alerts.stream()
                .filter(alert -> alert.signal() == Signal.BULLISH)
                .count();

        long bearishCount = alerts.stream()
                .filter(alert -> alert.signal() == Signal.BEARISH)
                .count();

        long watchCount = alerts.size() - bullishCount - bearishCount;

        StringBuilder report = new StringBuilder();
        report.append("📈 STOCK NEWS SCANNER\n\n")
                .append("Generated: ")
                .append(TIMESTAMP_FORMAT.format(summary.generatedAt()))
                .append('\n')
                .append("Universe scanned: ")
                .append(summary.scannedSymbols())
                .append(" stocks\n")
                .append("Important alerts: ")
                .append(summary.stocksWithImportantNews())
                .append("\n\n")
                .append("🟢 Bullish: ")
                .append(bullishCount)
                .append("   🔴 Bearish: ")
                .append(bearishCount)
                .append("   🟡 Watch: ")
                .append(watchCount)
                .append("\n\n")
                .append(DIVIDER)
                .append("\n\n");

        if (alerts.isEmpty()) {
            return report.append("No important news found.\n").toString();
        }

        for (Alert alert : alerts) {
            report.append(alert.signal().emoji())
                    .append(' ')
                    .append(alert.ticker())
                    .append(" — ")
                    .append(alert.sector())
                    .append('\n')
                    .append("Score: ")
                    .append(alert.score())
                    .append(" · ")
                    .append(alert.catalyst())
                    .append("\n\n");
        }

        report.append(DIVIDER)
                .append("\n\nDETAILS\n\n");

        for (Alert alert : alerts) {
            report.append(alert.signal().emoji())
                    .append(' ')
                    .append(alert.ticker())
                    .append(" — ")
                    .append(alert.sector())
                    .append(" — ")
                    .append(alert.signal().name())
                    .append(" · Score ")
                    .append(alert.score())
                    .append('\n')
                    .append(alert.headlines())
                    .append("\n\n");
        }

        return report.toString();
    }

    private Alert toAlert(String ticker, String summary) {
        String[] parts = summary.split(" \\| ", 3);

        Signal signal = Signal.fromSummary(parts.length > 0 ? parts[0] : null);
        int score = extractScore(parts.length > 1 ? parts[1] : "");
        String headlines = parts.length > 2 ? parts[2] : summary;

        return new Alert(
                ticker,
                findSector(ticker),
                signal,
                score,
                resolveCatalyst(headlines),
                headlines
        );
    }

    private String findSector(String ticker) {
        return StockSectors.getSectors().entrySet().stream()
                .filter(entry -> List.of(entry.getValue()).contains(ticker))
                .map(Map.Entry::getKey)
                .map(this::formatSector)
                .findFirst()
                .orElse("Unknown");
    }

    private int extractScore(String scorePart) {
        String prefix = "Score:";

        if (!scorePart.startsWith(prefix)) {
            return 0;
        }

        try {
            return Integer.parseInt(scorePart.substring(prefix.length()).trim());
        } catch (NumberFormatException exception) {
            return 0;
        }
    }

    private String resolveCatalyst(String text) {
        String normalized = text.toLowerCase(Locale.ROOT);

        if (containsAny(normalized,
                "upgrade", "upgraded",
                "downgrade", "downgraded",
                "initiates coverage", "initiated with")) {
            return "Analyst rating change";
        }

        if (containsAny(normalized,
                "price target", "target raised",
                "target cut", "target lowered",
                "pt raised", "pt cut")) {
            return "Price-target change";
        }

        if (containsAny(normalized,
                "earnings", "revenue", "eps",
                "quarterly results", "financial results")) {
            return "Earnings / guidance";
        }

        if (containsAny(normalized,
                "guidance", "forecast", "outlook")) {
            return "Guidance / outlook";
        }

        if (containsAny(normalized,
                "acquisition", "acquire",
                "merger", "buyout", "takeover")) {
            return "Acquisition / merger";
        }

        if (containsAny(normalized,
                "fda", "clinical trial",
                "phase 3", "phase iii",
                "approval", "approved", "rejected")) {
            return "FDA / clinical";
        }

        if (containsAny(normalized,
                "contract", "contract award",
                "awarded contract", "wins contract",
                "secured contract")) {
            return "Major contract";
        }

        if (containsAny(normalized,
                "stock split", "share split",
                "reverse split", "split ratio")) {
            return "Stock split";
        }

        return "Important company news";
    }

    private boolean containsAny(String text, String... keywords) {
        for (String keyword : keywords) {
            if (text.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    private String formatSector(String sector) {
        return switch (sector) {
            case "consumerGoods" -> "Consumer Goods";
            case "realEstate" -> "Real Estate";
            default -> sector.substring(0, 1).toUpperCase(Locale.ROOT)
                    + sector.substring(1);
        };
    }

    private enum Signal {
        BULLISH("🟢"),
        BEARISH("🔴"),
        WATCH("🟡");

        private final String emoji;

        Signal(String emoji) {
            this.emoji = emoji;
        }

        public static Signal fromSummary(String value) {
            try {
                return Signal.valueOf(
                        value == null ? "WATCH" : value.trim().toUpperCase(Locale.ROOT)
                );
            } catch (IllegalArgumentException exception) {
                return WATCH;
            }
        }

        public String emoji() {
            return emoji;
        }
    }

    private record Alert(
            String ticker,
            String sector,
            Signal signal,
            int score,
            String catalyst,
            String headlines
    ) {
    }
}