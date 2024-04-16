package mate.academy.bookingservice.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

// @Configuration
@Data
public class BotConfig {

    @Value("${BOT_NAME}")
    private String botName;

    @Value("${BOT_TOKEN}")
    private String token;
}
