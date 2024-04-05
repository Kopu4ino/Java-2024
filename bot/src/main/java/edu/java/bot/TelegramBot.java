package edu.java.bot;

import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.Processors.UserMessageProcessor;
import edu.java.bot.configuration.ApplicationConfig;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class TelegramBot extends com.pengrad.telegrambot.TelegramBot {
    @Autowired
    private final UserMessageProcessor userMessageProcessor;

    public TelegramBot(@Autowired ApplicationConfig config, UserMessageProcessor userMessageProcessor) {
        super(config.telegramToken());
        this.userMessageProcessor = userMessageProcessor;
        this.init();
    }

    private void init() {
        this.setUpdatesListener(updates -> {
            for (Update update : updates) {
                log.info("UPDATE:" + update);
                SendMessage sendMessage = userMessageProcessor.process(update);
                this.execute(sendMessage);

            }
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }
}
