package com.ktotopawel.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.jackson3.Jackson3Config;
import org.jdbi.v3.jackson3.Jackson3Plugin;
import org.jdbi.v3.postgres.PostgresPlugin;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import tools.jackson.databind.ObjectMapper;

public class AppDbConfig {

    public static Jdbi createJdbi(ObjectMapper mapper) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:postgresql://localhost:5432/jobqueue_db");
        config.setUsername("admin");
        config.setPassword("Passwordzaq123");

        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setIdleTimeout(30000);
        config.setConnectionTimeout(20000);

        HikariDataSource dataSource = new HikariDataSource(config);

        Jdbi jdbi = Jdbi.create(dataSource);

        jdbi
                .installPlugin(new SqlObjectPlugin())
                .installPlugin(new PostgresPlugin())
                .installPlugin(new Jackson3Plugin())
                .getConfig(Jackson3Config.class).setMapper(mapper);

        return jdbi;
    }
}
