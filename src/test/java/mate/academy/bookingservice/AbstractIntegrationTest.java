package mate.academy.bookingservice;

import mate.academy.bookingservice.config.TestContainersConfig;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { TestContainersConfig.class })
@ActiveProfiles("test")
public abstract class AbstractIntegrationTest {
}
