# Stock News Scanner

Free Spring Boot application that scans a built-in sector stock universe, fetches recent Google News RSS headlines, filters material headlines with Java rules, and returns a `Map<String, String>`.

- **Key:** ticker, for example `NVDA`
- **Value:** summary of recent material-news headlines

This is a research tool only. It does not trade or provide investment advice.

## Free-only architecture

```text
StockSectors -> Google News RSS -> Java keyword scoring -> Map<String, String>
```

No Gemini key, paid API, database, or cloud service is required.

## Requirements

- Java 17+
- Maven 3.9+

## Run

```bash
mvn spring-boot:run
```

The service starts on `http://localhost:8080`.

## Endpoints

### Scan custom symbols

```bash
curl -X POST http://localhost:8080/api/v1/stocks/scan \
  -H "Content-Type: application/json" \
  -d '{"symbols":["NVDA","AAPL","MSFT","LLY"]}'
```

### Scan one sector

```bash
curl -X POST http://localhost:8080/api/v1/stocks/scan/sector/technology
```

Supported sectors: `technology`, `finance`, `consumerGoods`, `media`, `healthcare`, `telecom`, `energy`, `industrials`, `materials`, `realEstate`, `utilities`.

### Scan all configured tickers

```bash
curl -X POST http://localhost:8080/api/v1/stocks/scan/all
```

A full scan makes many RSS requests and can take a few minutes. Test a sector first.

## Response shape

```json
{
  "stockNews": {
    "NVDA": "Importance score: 3 | [News source] NVDA headline"
  },
  "scannedSymbols": 145,
  "stocksWithImportantNews": 1,
  "generatedAt": "2026-08-21T00:00:00Z"
}
```

A ticker is included only when at least one recent headline reaches the configured importance threshold.

## Configuration

```yaml
scanner:
  lookback-hours: 24
  max-symbols-per-request: 200
  delay-between-symbols-ms: 400
  minimum-importance-score: 2
  maximum-headlines-per-stock: 3
```

Google News RSS is suitable for a personal prototype. Headlines may be delayed, incomplete, duplicated, or irrelevant. Read the original article before making any investment decision.
