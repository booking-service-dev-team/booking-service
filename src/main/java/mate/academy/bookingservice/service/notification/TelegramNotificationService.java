package mate.academy.bookingservice.service.notification;

import lombok.RequiredArgsConstructor;
import mate.academy.bookingservice.service.telergam.TelegramBot;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TelegramNotificationService implements NotificationService {
    private final TelegramBot bot;
    @Override
    public void sendMessageToAdmins(String message) {
        bot.sendMessageToAdmins(message);
    }
}
