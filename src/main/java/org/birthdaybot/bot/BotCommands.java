package org.birthdaybot.bot;

import org.birthdaybot.config.BotConfig;
import org.birthdaybot.entity.Group;
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
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramBot;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author Malikov Azizjon    birthday-bot       25.02.2025       12:15
 */

@Component
public class BotCommands extends TelegramLongPollingBot {
    final BotConfig config;

    @Autowired
    private UserService userService;

    @Autowired
    private GroupService groupService;

    public BotCommands(BotConfig config) {
        this.config = config;
    }

    private Map<Long, String> userStep = new HashMap<>();

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
        if (update.hasMessage() && update.getMessage().hasText()) {
            Long chatId = update.getMessage().getChatId();
            String messageText = update.getMessage().getText();

            switch (userStep.getOrDefault(chatId, "")) {
                case "ASK_NAME":
                    askForBirthday(chatId, messageText);
                    break;
                case "ASK_BIRTHDAY":
                    handleAddCommand(chatId, messageText);
                    break;
                default:
                    if (messageText.equals("/add")) {
                        askForName(chatId);
                    } else {
                        sendMessage(chatId, "Noma'lum buyruq.");
                    }
            }
        }
    }

    private void askForName(Long chatId) {
        sendMessage(chatId, "Iltimos, ismingiz va familiyangizni kiriting:");
        userStep.put(chatId, "ASK_NAME");
    }

    private void askForBirthday(Long chatId, String name) {
        sendMessage(chatId, "Iltimos, tug'ilgan sanangizni DD.MM.YYYY formatida kiriting:");
        userStep.put(chatId, "ASK_BIRTHDAY");
        userStep.put(Long.valueOf(chatId + "_name"), name);
    }

    private void handleAddCommand(Long chatId, String dateString) {
        String name = userStep.get(chatId + "_name");
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            LocalDate birthDate = LocalDate.parse(dateString, formatter);

            User user = User.builder()
                    .telegramId(chatId)
                    .username(name)
                    .birthDate(birthDate)
                    .build();

            userService.saveUser(user);
            sendMessage(chatId, "Ma'lumotlaringiz muvaffaqiyatli saqlandi!");

            clearChatHistory(chatId);

        } catch (DateTimeParseException e) {
            sendMessage(chatId, "Noto‘g‘ri sana formati. Format: DD.MM.YYYY");
        }
        userStep.remove(chatId);
        userStep.remove(chatId + "_name");
    }

    private void clearChatHistory(Long chatId) {
        try {
            Thread.sleep(2000); // 2 sekund kutish
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        SendMessage clearMessage = new SendMessage();
        clearMessage.setChatId(chatId.toString());
        clearMessage.setText("❌ Suhbat o'chirildi.");
        try {
            execute(clearMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveUserInGroup(Message message) {
        Long groupId = message.getChat().getId();
        Long userId = message.getFrom().getId().longValue();
        String username = message.getFrom().getUserName() != null ? message.getFrom().getUserName() : "Noma'lum";

        Optional<User> existingUser = userService.findUsersByTelegramId(userId);
        if (existingUser.isEmpty()) {
            Group group = groupService.findByTelegramGroupId(groupId).orElse(null);
            if (group != null) {
                User user = User.builder()
                        .telegramId(userId)
                        .username(username)
                        .group(group)
                        .build();

                userService.saveUser(user);
                sendMessage(groupId, "Yangi foydalanuvchi saqlandi: " + username);
            }
        }
    }


    private void handleAddCommand(Long chatId, String name, String dateString) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            LocalDate birthDate = LocalDate.parse(dateString, formatter);

            User user = User.builder()
                    .telegramId(chatId)
                    .username(name)
                    .birthDate(birthDate)
                    .build();

            userService.saveUser(user);
            sendMessage(chatId, "Ma'lumotlar saqlandi!");
            clearChatHistory(chatId);
        } catch (DateTimeParseException e) {
            sendMessage(chatId, "Noto‘g‘ri sana formati. Format: DD.MM.YYYY");
        }
    }

    private void handleUpdateCommand(Long chatId, String updateField, String updateValue) {
        if (updateField.equalsIgnoreCase("ism")) {
            userService.updateUserName(chatId, updateValue);
            sendMessage(chatId, "Ismingiz yangilandi!");
        } else if (updateField.equalsIgnoreCase("tug'ilgan kun")) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                LocalDate newBirthDate = LocalDate.parse(updateValue, formatter);
                userService.updateUser(chatId, newBirthDate);
                sendMessage(chatId, "Tug'ilgan kun yangilandi!");
            } catch (DateTimeParseException e) {
                sendMessage(chatId, "Noto‘g‘ri sana formati. Format: DD.MM.YYYY");
            }
        }
        clearChatHistory(chatId);
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
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}