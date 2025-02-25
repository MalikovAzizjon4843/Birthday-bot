package org.birthdaybot.config;
import org.birthdaybot.bot.BotCommands;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

/**
 * @author Malikov Azizjon    birthday-bot       25.02.2025       16:00
 */

@Configuration
public class TelegramBotInitializer {
    @Bean
    public TelegramBotsApi telegramBotsApi(BotCommands botCommands) throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(botCommands);
        return telegramBotsApi;
    }
}
