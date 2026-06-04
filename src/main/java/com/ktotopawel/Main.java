import com.ktotopawel.config.AppDbConfig;
import io.javalin.Javalin;
import org.jdbi.v3.core.Jdbi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

void main() {
    Logger logger = LoggerFactory.getLogger("Main");

    logger.info("Starting application...");
    logger.info("Initializing database connection...");
    Jdbi jdbi = AppDbConfig.createJdbi();
    logger.info("Database connection initialized successfully.");

}