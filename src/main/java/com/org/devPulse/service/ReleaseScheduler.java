package com.org.devPulse.service;

import com.org.devPulse.entity.LastSeenRelease;
import com.org.devPulse.repository.LastSeenReleaseRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReleaseScheduler {
    private final RssReader reader;
    private final ReleaseFilter filter;
    private final TelegramService telegram;
    private final LastSeenReleaseRepo repo;

    @Scheduled(fixedRate = 600_000)
    public void checkReleases() throws Exception {

        Map<String, String> feeds = Map.of(
                "Spring Boot", "https://github.com/spring-projects/spring-boot/releases.atom",
                "Spring Framework", "https://github.com/spring-projects/spring-framework/releases.atom"
        );

        feeds.forEach((feedName, url) -> {
            try {
                reader.read(url).forEach(entry -> {
                    String link = entry.getLink();

                    LastSeenRelease record = repo.findById(feedName).orElse(null);

                    if (record == null || !link.equals(record.getLastReleaseId())) {
                        // send alert
                        telegram.send(feedName + " → " + entry.getTitle() + "\n" + link);

                        // update or insert
                        LastSeenRelease r = new LastSeenRelease();
                        r.setFeedName(feedName);
                        r.setLastReleaseId(link);
                        repo.save(r);
                    } else {
                        log.info("No new release for {}. Last seen: {}", feedName, record.getLastReleaseId());
                    }
                });
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}

