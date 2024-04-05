package edu.java.bot.Comands;

import com.pengrad.telegrambot.model.MessageEntity;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.Entity.EntityType;
import edu.java.bot.Repository.UserRepository;
import edu.java.bot.ResponseMessage.StandardResponseMessage;
import edu.java.bot.api.client.ScrapperClient;
import edu.java.bot.services.exceptions.ApiErrorException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shared.dto.request.RemoveLinkRequest;
import static edu.java.bot.Utils.EntityUtils.getEntityByName;
import static edu.java.bot.Utils.EntityUtils.getTextFromEntity;

@Component
@RequiredArgsConstructor
@Log4j2
public class UntrackCommand implements Command {
    @Autowired
    UserRepository userRepository;

    @Autowired
    private final ScrapperClient scrapperClient;

    @Override
    public SendMessage handle(Update update) {
        String message;
        Long chatId = update.message().chat().id();

        MessageEntity urlEntity = getEntityByName(update, EntityType.URL.getName());
        if (urlEntity != null) {
            String urlText = getTextFromEntity(update, urlEntity);

            try {
                scrapperClient.removeLink(chatId, new RemoveLinkRequest(urlText));
                message = StandardResponseMessage.LINK_NO_LONGER_TRACKED.getMessage();
            } catch (ApiErrorException e) {
                log.warn(e.getApiErrorResponse().description());
                message = e.getApiErrorResponse().exceptionMessage();
            }
        } else {
            message = StandardResponseMessage.LINK_NOT_RECOGNIZED.getMessage();
        }
        return new SendMessage(chatId, message);
    }

    @Override
    public String getName() {
        return CommandType.UNTRACK.getName();
    }
}
