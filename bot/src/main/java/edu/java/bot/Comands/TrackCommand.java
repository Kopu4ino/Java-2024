package edu.java.bot.Comands;

import com.pengrad.telegrambot.model.MessageEntity;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.Entity.EntityType;
import edu.java.bot.Links.LinkHandler;
import edu.java.bot.ResponseMessage.StandardResponseMessage;
import edu.java.bot.api.client.ScrapperClient;
import edu.java.bot.services.exceptions.ApiErrorException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shared.dto.request.AddLinkRequest;
import static edu.java.bot.Utils.EntityUtils.getEntityByName;
import static edu.java.bot.Utils.EntityUtils.getTextFromEntity;

@Component
@RequiredArgsConstructor
@Log4j2
public class TrackCommand implements Command {

    @Autowired
    private final ScrapperClient scrapperClient;

    @Autowired
    private final List<LinkHandler> handlers;

    @Override
    public SendMessage handle(Update update) {
        Long chatId = update.message().chat().id();
        String message;

        MessageEntity urlEntity = getEntityByName(update, EntityType.URL.getName());

        if (urlEntity != null) {
            String urlText = getTextFromEntity(update, urlEntity);
            boolean isCanHandle = false;
            try {
                URI uri = new URI(urlText);
                LinkHandler chain = buildChain();
                isCanHandle = chain.handle(uri);
            } catch (URISyntaxException e) {
                log.error("Ошибка преобразования ссылки в uri. Ссылка: " + urlText);
                throw new RuntimeException(e);
            }

            if (isCanHandle) {
                try {
                    scrapperClient.addLink(chatId, new AddLinkRequest(urlText));
                    message = StandardResponseMessage.LINK_ADDED_SUCCESSFULLY.getMessage();
                } catch (ApiErrorException e) {
                    log.warn(e.getApiErrorResponse().description());
                    message = e.getApiErrorResponse().exceptionMessage();
                }
            } else {
                message = StandardResponseMessage.LINK_PROCESSING_FAILED.getMessage();
            }
        } else {
            message = StandardResponseMessage.ADD_LINK_COMMAND_USAGE.getMessage();
        }

        return new SendMessage(chatId, message);
    }

    public LinkHandler buildChain() {
        for (int i = 0; i < handlers.size() - 1; i++) {
            handlers.get(i).setNext(handlers.get(i + 1));
        }
        return handlers.isEmpty() ? null : handlers.getFirst();
    }

    @Override
    public String getName() {
        return CommandType.TRACK.getName();
    }
}
