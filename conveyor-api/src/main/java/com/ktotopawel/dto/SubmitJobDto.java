package com.ktotopawel.dto;

import java.util.Map;

public record SubmitJobDto(String name, Map<String, Object> jobData) {
}
