package com.sameer.stockscanner.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record ScanRequest(
        @NotEmpty(message = "symbols must not be empty")
        @Size(max = 200, message = "maximum 200 symbols per request")
        List<String> symbols
) {
}
