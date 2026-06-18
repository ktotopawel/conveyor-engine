package com.ktotopawel.signaler;

import com.ktotopawel.worker.Worker;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import org.postgresql.PGConnection;
import org.postgresql.PGNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class Signaler implements Runnable {

    private final Logger logger = LoggerFactory.getLogger(Signaler.class);
    private final List<Worker> subscribed = new ArrayList<>();
    private final HikariDataSource dataSource;

    public void subscribe(Worker worker) {
        subscribed.add(worker);
    }

    private void signal(PGNotification notification) {
        for (Worker worker : subscribed) {
            worker.externalSignal(notification);
        }
    }

    @Override
    public void run() {
        logger.info("Signaler started.");
        try (Connection connection = dataSource.getConnection()) {
            PGConnection pgConnection = connection.unwrap(PGConnection.class);

            logger.info("Signaler established connection to DB.");

            try (Statement statement = connection.createStatement()) {
                statement.execute("LISTEN job_notifications");
            }

            logger.info("Signaler listening on job_notifications.");

            while (!Thread.currentThread().isInterrupted()) {
                PGNotification[] notifications = pgConnection.getNotifications(10_000);

                if (notifications != null) {
                    for (PGNotification n : notifications) {
                        signal(n);
                    }
                }
            }

            logger.error("Connection with db interrupted");

            // todo: handle connection interrupt
        } catch (SQLException e) {
            logger.error("Error with the database: {}", e.toString());
            // todo: add sql connection break handling
        }
    }
}
