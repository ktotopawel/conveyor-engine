package com.ktotopawel.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jdbi.v3.core.Jdbi;

public class AppDbConfig {

    public static Jdbi createJdbi() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:postgresql://localhost:5432/your_database");
        config.setUsername("admin");
        config.setPassword("Passwordzaq123");

        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setIdleTimeout(30000);
        config.setConnectionTimeout(20000);

        HikariDataSource dataSource = new HikariDataSource(config);

        return Jdbi.create(dataSource);
    }
}
