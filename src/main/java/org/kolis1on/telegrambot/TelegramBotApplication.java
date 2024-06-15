package org.kolis1on.telegrambot;

import org.kolis1on.telegrambot.controller.MyTelegramBot;
import org.kolis1on.telegrambot.service.InviteesService;
import org.kolis1on.telegrambot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@SpringBootApplication
public class TelegramBotApplication {

	private final UserService userService;
	private final InviteesService inviteesService;

	@Autowired
	public TelegramBotApplication(UserService userService, InviteesService inviteesService) {
		this.userService = userService;
		this.inviteesService = inviteesService;
	}

	public static void main(String[] args) {
		SpringApplication.run(TelegramBotApplication.class, args);
	}

	// Register Telegram bot using dependency injection
	@Bean
	public MyTelegramBot myTelegramBot() {
		return new MyTelegramBot(userService, inviteesService);
	}

	// Register the bot with TelegramBotsApi
	@Bean
	public TelegramBotsApi telegramBotsApi() throws TelegramApiException {
		TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
		botsApi.registerBot(myTelegramBot()); // Injecting bot instance
		return botsApi;
	}
}

