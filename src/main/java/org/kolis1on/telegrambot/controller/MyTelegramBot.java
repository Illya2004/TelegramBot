package org.kolis1on.telegrambot.controller;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.kolis1on.telegrambot.entity.Invitees;
import org.kolis1on.telegrambot.entity.User;
import org.kolis1on.telegrambot.service.InviteesService;
import org.kolis1on.telegrambot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.groupadministration.CreateChatInviteLink;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.ChatInviteLink;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@EqualsAndHashCode(callSuper = true)
@Slf4j
@Data
@Component
@AllArgsConstructor
public class MyTelegramBot extends TelegramLongPollingBot {

	private final UserService userService;

	private final InviteesService inviteesService;



    @Override
	public void onUpdateReceived(Update update) {
		log.info("Update received: {}", update);
//		if (!update.hasMessage() || !update.getMessage().hasText()) {
//			return;
//		}
		if(update.hasChatMember() && update.getChatMember().getOldChatMember().getStatus().equals("member") &&
				update.getChatMember().getNewChatMember().getStatus().equals("left")){

			String username = update.getChatMember().getFrom().getUserName();
			if(username == null){
				username = update.getChatMember().getFrom().getFirstName();
			}

			long chatId = update.getChatMember().getChat().getId();
			System.out.println(chatId);
			if(chatId == -1002217627706l && inviteesService.ifExists(username)) {
				User user = inviteesService.getUserIdByInvitees(username);
				user.decrementPeopleJoined();
			}
			System.out.println(1);
			if(chatId == -1002217627706l){
				if(!userService.ifUserExist(username)) return;

				User user = userService.findByUsername(username);
				user.setChannelFollow(false);
				user.setIfParticipate(false);

				userService.save(user);

				System.out.println("channel");
			}
			if(chatId == -1002217382503l){
				if(!userService.ifUserExist(username)) return;

				User user = userService.findByUsername(username);
				user.setChatFollow(false);
				user.setIfParticipate(false);

				userService.save(user);

				System.out.println("chat");
			}

		}
		if(update.hasChatJoinRequest()){
			String link = update.getChatJoinRequest().getInviteLink().getInviteLink();
			String username = "";

			if(update.getChatJoinRequest().getUser().getUserName() == null){
				username = update.getChatJoinRequest().getUser().getFirstName();
			}else{
				username = update.getChatJoinRequest().getUser().getUserName();
			}
			if(userService.existsByLink(link) && !inviteesService.ifExists(username)){

				userService.incrementPeople(link);
				inviteesService.save(username, link);
			}
		}

		if(update.hasMessage() && update.getMessage().getText() != null && update.getMessage().getText().equals("/start")) {
			Message message = update.getMessage();
			String chatId = message.getChatId().toString();
			String responseText = "*Привет,* @" + message.getFrom().getUserName() + "!\n\n" +
					"Нужны рефералы в *Hamster Kombat*\uD83D\uDC39, либо в другой игре?\n" +
					"Принимай участие в розыгрыше и выигрывай до *10* рефералов!\n\n" +
					"\uD83E\uDEB5*Условия конкурса:*\n" +
					" - Вступить в наш телеграм канал по [ссылке](https://t.me/+fZz8yZcf1HhjYTBi)\uD83D\uDC48\n" +
					" - Присоединится в наш групповой чат по [ссылке](https://t.me/+HMbWJnhY45VlZDBi)\uD83D\uDC48\n" +
					" - Пригласить не менее 2 друзей в тгк (сгенерируй ссылку приглашения по кнопке)\uD83D\uDC7B\n\n" +
					"`Чем больше друзей ты пригласишь, тем больше шансов у тебя выиграть в розыгрыше\uD83C\uDF7E`\n\n" +
					"*Результаты конкурса 23.06 в 18:00*\uD83C\uDF1D\n\n" +
					"_Желаю победы!_";

			SendMessage response = new SendMessage();
			response.setChatId(chatId);
			response.setText(responseText);
			response.setParseMode("Markdown");
			response.disableWebPagePreview();

			// Create inline keyboard
			InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
			List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
			List<InlineKeyboardButton> rowInline = new ArrayList<>();
			InlineKeyboardButton button1 = new InlineKeyboardButton();
			button1.setText("Сгенерировать ссылку\uD83E\uDDE4");
			button1.setCallbackData("generateLink");

			InlineKeyboardButton button2 = new InlineKeyboardButton();
			button2.setText("Прогресс\uD83E\uDDF3");
			button2.setCallbackData("progress");

			rowInline.add(button1);
			rowInline.add(button2);
			// Add the row to the keyboard
			rowsInline.add(rowInline);
			// Set the keyboard to the markup
			inlineKeyboardMarkup.setKeyboard(rowsInline);
			// Attach the keyboard to the message
			response.setReplyMarkup(inlineKeyboardMarkup);

			try {
				execute(response);
				log.info("Sent message to chat id: {}", chatId);
			} catch (TelegramApiException e) {
				log.error("Error sending message", e);
			}
		}

		// buttons
		if (update.hasCallbackQuery()) {

			String callbackData = update.getCallbackQuery().getData();
			Long chatId = update.getCallbackQuery().getMessage().getChatId();
			String text = "";
			String username = update.getCallbackQuery().getFrom().getUserName();
			if(username == null){
				username = update.getCallbackQuery().getFrom().getFirstName();
			}
			if(callbackData.equals("generateLink")){
				if(!userService.ifUserExist(username)) {
					String link = createInviteLink("-1002217627706", username);
					text = "Ваша пригласительная ссылка - " + link;

					userService.addUser(username, link);
				}else{
					text = "Вы уже генерировали ссылку, посмотрите в *\"Прогрес\uD83E\uDDF3*\"";
				}
			}

			if(callbackData.equals("progress")){
				if(userService.ifUserExist(username)) {
					User user = userService.findByUsername(username);
					text = "*\uD83E\uDEA2Пригласительная ссылка -* " + user.getInvitationLink() + "\n\n";

					StringBuilder stringBuilder = new StringBuilder(text);
					stringBuilder.append(progressConditions(user, update.getCallbackQuery().getFrom().getId()));

					text = stringBuilder.toString();
				}else{
					text = "*Вы не можете принимать участие в розыгрыше, сгенерируйте ссылку*\uD83D\uDD17";
				}
			}

			SendMessage response = new SendMessage();
			response.setChatId(chatId);
			response.setText(text);
			response.setParseMode("Markdown");
			response.disableWebPagePreview();

			try {
				execute(response);
				log.info("Sent message to chat id: {}", chatId);
			} catch (TelegramApiException e) {
				log.error("Error sending message", e);
			}
		} else {
			log.info("Update does not contain a text message");
		}


	}

	private String progressConditions(User user, Long userId){
		StringBuilder stringBuilder = new StringBuilder();
		boolean firstCond = false;
		boolean secondCond = false;
		boolean thirdCond = false;

		if(playerIsInChannel(userId, -1002217627706l)){
			user.setChannelFollow(true);
			firstCond = true;
			stringBuilder.append("1. Участник телеграм канала - Да✅\n");
		}else{
			stringBuilder.append("1. Участник телеграм канала - Нет❌\n");
		}
		if(playerIsInChannel(userId, -1002217382503l)){
			thirdCond = true;
			user.setChatFollow(true);
			stringBuilder.append("2. Вступил в чат - Да✅\n");

		}else{
			stringBuilder.append("2. Вступил в чат - Нет❌\n");

		}
		if(user.getPeopleJoined() >= 2 ){
			secondCond = true;
			stringBuilder.append("2. Пригласил 2-их друзей ("+user.getPeopleJoined()+") - Да✅");
		}else{
			stringBuilder.append("2. Пригласил 2-их друзей ("+user.getPeopleJoined()+") - Нет❌\n\n");
		}

		if(firstCond && secondCond && thirdCond){
			user.setIfParticipate(true);
			stringBuilder.append("*Участвую в розыгрыше - Да*\uD83C\uDF4F");
		}else{
			stringBuilder.append("*Участвую в розыгрыше - Нет*❌");
		}

		userService.save(user);
		return stringBuilder.toString();
	}

	public boolean playerIsInChannel(Long userId, Long channelId) {
		GetChatMember getChatMember = new GetChatMember();
		getChatMember.setChatId(channelId);
		getChatMember.setUserId(userId);

		try {
			ChatMember chatMember = execute(getChatMember);
			if (chatMember != null && chatMember.getStatus() != null) {
				String status = chatMember.getStatus();
				return status.equals("creator") || status.equals("administrator") || status.equals("member");
			}
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
		return false;
	}

	private String createInviteLink(String channelId, String userName) {
		CreateChatInviteLink createChatInviteLink = new CreateChatInviteLink();
		createChatInviteLink.setChatId(channelId);
		createChatInviteLink.setCreatesJoinRequest(true);
		createChatInviteLink.setName(userName);
		try {
			ChatInviteLink inviteLink = execute(createChatInviteLink);

            return inviteLink.getInviteLink();
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
		return "";
	}
	@Override
	public String getBotUsername() {
		return "CryptoWheel52bot";
	}

	@Override
	public String getBotToken() {
		return "7283165498:AAFQMQxqy3ett-4NNi-1RDgwXwm6AsK1rzI";
	}
}
