package mate.academy.bookingservice.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration
public class TestContainersConfig {

    @Bean
    public PostgreSQLContainer<?> postgreSqlContainer() {
        PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
                DockerImageName.parse("postgres:latest")
        )
                .withDatabaseName("booking_service_test_db")
                .withUsername("postgres")
                .withPassword("postgres");
        postgres.start();
        return postgres;
    }
}
