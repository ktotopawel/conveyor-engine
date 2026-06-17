package com.ktotopawel.config.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.jackson3.Jackson3Config;
import org.jdbi.v3.jackson3.Jackson3Plugin;
import org.jdbi.v3.postgres.PostgresPlugin;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import tools.jackson.databind.ObjectMapper;

public class AppDbConfig {

    private static final String JDBC_URL = System.getenv().getOrDefault("DATABASE_URL", "jdbc:postgresql://localhost:5432/jobqueue_db");
    private static final String USERNAME = System.getenv().getOrDefault("DATABASE_USER", "admin");
    private static final String PASSWORD = System.getenv().getOrDefault("DATABASE_PASSWORD", "Passwordzaq123");

    @Builder(builderMethodName = "configure", buildMethodName = "create")
    public static Jdbi createJdbi(int maxPoolSize, int minIdle, ObjectMapper mapper) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(JDBC_URL);
        config.setUsername(USERNAME);
        config.setPassword(PASSWORD);

        config.setMaximumPoolSize(maxPoolSize);
        config.setMinimumIdle(minIdle);
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

    public static HikariDataSource createListenerDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(JDBC_URL);
        config.setUsername(USERNAME);
        config.setPassword(PASSWORD);
        config.setMaximumPoolSize(1);
        config.setMinimumIdle(1);
        return new HikariDataSource(config);
    }
}
