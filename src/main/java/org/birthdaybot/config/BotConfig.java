package org.birthdaybot.config;

import lombok.Data;
import org.birthdaybot.bot.BotCommands;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

/**
 * @author Malikov Azizjon    birthday-bot       25.02.2025       14:41
 */

@Configuration
@PropertySource("classpath:application.properties")
@Data
public class BotConfig {
    @Value("${telegram.bot.username}")
    String username;

    @Value("${telegram.bot.token}")
    String token;
}
