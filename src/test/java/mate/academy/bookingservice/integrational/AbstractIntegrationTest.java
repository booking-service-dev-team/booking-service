package mate.academy.bookingservice.integrational;

import mate.academy.bookingservice.config.TestContainersConfig;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { TestContainersConfig.class })
@ActiveProfiles("test")
public abstract class AbstractIntegrationTest {

    @Autowired
    private PostgreSQLContainer<?> postgreSqlContainer;

    @Autowired
    private ApplicationContext applicationContext;

    static {
        PostgreSQLContainer<?> postgreSqlContainer = new PostgreSQLContainer<>("postgres:latest")
                .withDatabaseName("booking_service_test_db")
                .withUsername("postgres")
                .withPassword("postgres");
        postgreSqlContainer.start();
        System.setProperty("DB_URL", postgreSqlContainer.getJdbcUrl());
        System.setProperty("DB_USERNAME", postgreSqlContainer.getUsername());
        System.setProperty("DB_PASSWORD", postgreSqlContainer.getPassword());
    }
}
