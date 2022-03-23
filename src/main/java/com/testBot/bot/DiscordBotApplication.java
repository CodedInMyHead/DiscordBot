package com.testBot.bot;

import com.testBot.bot.configuration.BotConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DiscordBotApplication {

	public static void main(String[] args) {
		BotConfiguration.token = args[0];
		SpringApplication.run(DiscordBotApplication.class, args);
	}

}
