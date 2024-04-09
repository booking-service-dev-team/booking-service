package mate.academy.bookingservice.service.telergam;

import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import mate.academy.bookingservice.config.BotConfig;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@RequiredArgsConstructor
@Component
public class TelegramBot extends TelegramLongPollingBot {

    private final BotConfig config;

    private Set<Long> adminChatIds = new HashSet<>();

    @Override
    public String getBotUsername() {

        return config.getBotName();
    }

    @Override
    public String getBotToken() {

        return config.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        String firstName = update.getMessage().getChat().getFirstName();
        String text = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();
        if (!adminChatIds.contains(chatId) && text.equals("/start")) {
            adminChatIds.add(chatId);
            sendMessage("Hello "
                    + firstName
                    + "! This chat is active!", chatId);
        }
    }

    public void sendMessageToAdmins(String message) {
        sendMessageToDifferentChats(message, adminChatIds);
    }

    private void sendMessage(String message, Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(message);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendMessageToDifferentChats(String message, Set<Long> chatIds) {
        chatIds.forEach(chatId -> sendMessage(message, chatId));
    }
}
