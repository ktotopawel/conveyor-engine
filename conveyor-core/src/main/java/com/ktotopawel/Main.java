import com.ktotopawel.config.AppDbConfig;
import com.ktotopawel.config.AppObjectMapperConfig;
import org.jdbi.v3.core.Jdbi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.databind.ObjectMapper;

void main() {
    Logger logger = LoggerFactory.getLogger("Main");

    logger.info("Starting application...");

    ObjectMapper mapper = AppObjectMapperConfig.createObjectMapper();

    logger.info("Initializing database connection...");
    Jdbi jdbi = AppDbConfig.createJdbi(mapper);
    logger.info("Database connection initialized successfully.");

}