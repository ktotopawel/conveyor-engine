package com.ktotopawel.model;

import org.jdbi.v3.json.Json;

import java.util.Map;
import java.util.UUID;

public record Job(UUID id, String name, @Json Map<String, Object> jobData) {

    public Job withId(UUID newId) {
        return new Job(newId, this.name, this.jobData);
    }
}

