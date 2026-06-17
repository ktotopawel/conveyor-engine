package com.ktotopawel.config;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class WorkerConfig {
    private int leaseDuration;
    private BackoffConfig backoffConfig;
}