package edu.java.bot.Comands;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.ResponseMessage.StandardResponseMessage;
import edu.java.bot.api.client.ScrapperClient;
import edu.java.bot.services.exceptions.ApiErrorException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StartCommand implements Command {
    @Autowired final ScrapperClient scrapperClient;

    @Override
    public SendMessage handle(Update update) {
        Long chatId = update.message().chat().id();

        try {
            scrapperClient.registerChat(chatId);
        } catch (ApiErrorException exception) {
            return new SendMessage(chatId, StandardResponseMessage.ALREADY_REGISTERED.getMessage());
        }

        return new SendMessage(chatId, StandardResponseMessage.SUCCESS_REGISTRATION.getMessage());

    }

    @Override
    public String getName() {
        return CommandType.START.getName();
    }
}
