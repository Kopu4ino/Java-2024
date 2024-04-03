package edu.java.bot.Comands;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.ResponseMessage.StandardResponseMessage;
import edu.java.bot.api.client.ScrapperClient;
import edu.java.bot.services.exceptions.ApiErrorException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shared.dto.response.LinkResponse;

@Component
@RequiredArgsConstructor
@Log4j2
public class ListCommand implements Command {

    @Autowired
    private final ScrapperClient scrapperClient;

    @Override
    public SendMessage handle(Update update) {
        Long chatId = update.message().chat().id();
        String message;

        try {
            List<LinkResponse> chatLinks = scrapperClient.getLinks(chatId);
            if (chatLinks.isEmpty()) {
                message = StandardResponseMessage.NO_SERVICES_BEING_TRACKED.getMessage();
            } else {
                StringBuilder listOfUrl = new StringBuilder();
                chatLinks.forEach(
                    (link) -> {
                        listOfUrl.append(link.url()).append("\n");
                    }
                );
                message = listOfUrl.toString();
            }
        } catch (ApiErrorException e) {
            log.error(e.getApiErrorResponse().description());
            message = e.getMessage();
        }

        return new SendMessage(chatId, message);
    }

    @Override
    public String getName() {
        return CommandType.LIST.getName();
    }
}
