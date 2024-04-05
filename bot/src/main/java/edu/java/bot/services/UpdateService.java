package edu.java.bot.services;

import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.TelegramBot;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shared.dto.request.LinkUpdateRequest;

@Service
@RequiredArgsConstructor
public class UpdateService {
    private final TelegramBot bot;

    public void processUpdate(LinkUpdateRequest updateRequest) {
        String message = "По ссылке %s произошли изменения:\n%s"
            .formatted(updateRequest.url(), updateRequest.description());

        updateRequest.tgChatIds().forEach((telegramId) ->
            bot.execute(new SendMessage(telegramId, message)));
    }

}
