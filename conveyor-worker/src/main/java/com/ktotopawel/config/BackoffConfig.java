package com.ktotopawel.config;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Builder
@Getter
@NoArgsConstructor  // Generates a public BackoffConfig()
@AllArgsConstructor // Required by Lombok when combining @Builder and @NoArgsConstructor
public class BackoffConfig {
    @Builder.Default
    private long initialDelayMillis = 100;
    @Builder.Default
    private long maxDelayMillis = 60000;
    @Builder.Default
    private int exponent = 2;
}
