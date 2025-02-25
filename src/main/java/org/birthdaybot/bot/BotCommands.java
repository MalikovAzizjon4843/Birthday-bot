package org.birthdaybot.bot;

import org.birthdaybot.config.BotConfig;
import org.birthdaybot.entity.User;
import org.birthdaybot.services.GroupService;
import org.birthdaybot.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.generics.TelegramBot;

import java.time.LocalDate;

/**
 * @author Malikov Azizjon    birthday-bot       25.02.2025       12:15
 */

@Component
public class BotCommands extends TelegramLongPollingBot {

    final BotConfig config;

    public BotCommands(BotConfig config) {
        this.config = config;
    }

    @Autowired
    private UserService userService;

    @Autowired
    private GroupService groupService;

    @Override
    public String getBotUsername() {
        return config.getUsername();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }


    @Override
    public void onUpdateReceived(Update update) {

        System.out.println("Keldi: " + update);
        if (update.hasMessage() && update.getMessage().hasText()) {
            String text = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();

            SendMessage message = new SendMessage();
            message.setChatId(chatId.toString());
            message.setText("Xabar olindi: " + text);


            try {
                execute(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();

            if (messageText.equals("/start")) {
                sendMessage(chatId, "Bot ishga tushdi!");
            } else if (messageText.startsWith("/add")) {
                handleAddCommand(update.getMessage());
            } else if (messageText.startsWith("/update")) {
                handleUpdateCommand(update.getMessage());
            } else if (messageText.startsWith("/delete")) {
                handleDeleteCommand(update.getMessage());
            } else {
                sendMessage(chatId, "Noma'lum buyruq.");
            }
        }
    }

    private void handleAddCommand(Message message) {
        String[] parts = message.getText().split(" ", 4);
        if (parts.length < 3) {
            sendMessage(message.getChatId(), "To'g'ri format: /add [Ism Familiya] [YYYY-MM-DD]");
            return;
        }

        String name = parts[1];
        LocalDate birthDate = LocalDate.parse(parts[2]);

        User user = User.builder()
                .telegramId(message.getFrom().getId().longValue())
                .username(name)
                .birthDate(birthDate)
                .build();

        userService.saveUser(user);
        sendMessage(message.getChatId(), "Ma'lumotlar saqlandi!");
    }

    private void handleUpdateCommand(Message message) {
        String[] parts = message.getText().split(" ", 2);
        if (parts.length < 2) {
            sendMessage(message.getChatId(), "To'g'ri format: /update [YYYY-MM-DD]");
            return;
        }

        LocalDate newBirthDate = LocalDate.parse(parts[1]);
        userService.updateUser(message.getFrom().getId().longValue(), newBirthDate);
        sendMessage(message.getChatId(), "Tug'ilgan kun yangilandi!");
    }

    private void handleDeleteCommand(Message message) {
        userService.deleteUser(message.getFrom().getId().longValue());
        sendMessage(message.getChatId(), "Ma'lumotlar o'chirildi!");
    }

    private void sendMessage(Long chatId, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId.toString());
        sendMessage.setText(text);
        try {
            execute(sendMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
