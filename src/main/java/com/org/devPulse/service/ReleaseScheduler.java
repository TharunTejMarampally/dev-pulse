package com.org.devPulse.service;

import com.org.devPulse.entity.LastSeenRelease;
import com.org.devPulse.repository.LastSeenReleaseRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;

import org.springframework.boot.CommandLineRunner;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReleaseScheduler {

    private final RssReader reader;
    private final ReleaseFilter filter;
    private final TelegramService telegram;
    private final LastSeenReleaseRepo repo;

    @Scheduled(fixedDelay = 600_000)
    public void checkReleases() {

        Map<String, String> feeds = Map.of(
                "Spring Boot", "https://github.com/spring-projects/spring-boot/releases.atom",
                "Spring Framework", "https://github.com/spring-projects/spring-framework/releases.atom"
        );

        feeds.forEach((feedName, url) -> {
            try {
                var entries = reader.read(url);

                if (entries.isEmpty()) return;

                var latest = entries.get(0);   // ✅ only latest
                String link = latest.getLink();

                LastSeenRelease record = repo.findById(feedName).orElse(null);

                if (record == null || !link.equals(record.getLastReleaseId())) {

                    telegram.send(feedName + " → " + latest.getTitle() + "\n" + link);

                    LastSeenRelease r = new LastSeenRelease();
                    r.setFeedName(feedName);
                    r.setLastReleaseId(link);
                    repo.save(r);

                } else {
                    log.info("No new release for {}", feedName);
                }

            } catch (Exception e) {
                log.error("Error checking {} : {}", feedName, e.getMessage());
            }
        });
    }
}
