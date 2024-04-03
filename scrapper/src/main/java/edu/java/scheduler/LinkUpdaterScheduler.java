package edu.java.scheduler;

import edu.java.api.client.BotClient;
import edu.java.configuration.ApplicationConfig;
import edu.java.service.updater.LinkUpdateService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import shared.dto.request.LinkUpdateRequest;

@Log4j2
@Component
@RequiredArgsConstructor
@EnableScheduling
@ConditionalOnProperty(value = "app.scheduler.enable", havingValue = "true", matchIfMissing = true)
public class LinkUpdaterScheduler {
    private final BotClient botWebClient;

    private final LinkUpdateService linkUpdateService;

    private final int updatesCount;

    private final long interval;

    @Autowired
    public LinkUpdaterScheduler(ApplicationConfig config, BotClient botClient, LinkUpdateService linkUpdateService) {
        this.botWebClient = botClient;
        this.linkUpdateService = linkUpdateService;
        this.updatesCount = config.updatesCount();
        this.interval = config.interval();
    }

    @Scheduled(fixedDelayString = "#{@scheduler.interval().toMillis()}")
    public void update() {
        log.info("Getting updates...");

        List<LinkUpdateRequest> updates = linkUpdateService.fetchAllUpdates(updatesCount, interval);
        updates.forEach((update) -> {
            String response = botWebClient.sendUpdate(update);
            log.info("Update for link with id %d: %s".formatted(update.id(), response));
        });
        log.info("Updates processed");

    }
}
