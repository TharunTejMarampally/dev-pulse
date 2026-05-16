package com.org.devPulse.service;

import com.org.devPulse.entity.LastSeenRelease;
import com.org.devPulse.repository.LastSeenReleaseRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReleaseScheduler {

    private final RssReader reader;
    private final TelegramService telegram;
    private final LastSeenReleaseRepo repo;
    private final SummaryService summaryService;

    @Scheduled(fixedDelay = 600_000)
    public void checkReleases() {

        Map<String, String> feeds = Map.of(
                "Spring Boot", "https://github.com/spring-projects/spring-boot/releases.atom",
                "Spring Framework", "https://github.com/spring-projects/spring-framework/releases.atom"
        );

        feeds.forEach((feedName, url) -> {
            try {
                var entries = reader.read(url);

                if (entries.isEmpty()) {
                    log.info("No entries found for {}", feedName);
                    return;
                }

                var latest = entries.get(0);
                String link = latest.getLink();

                LastSeenRelease record = repo.findById(feedName).orElse(null);

                if (record == null || !link.equals(record.getLastReleaseId())) {

                    log.info("New release found for {} → {}", feedName, latest.getTitle());

                    // 1. Fetch full article text
                    String articleText = fetch(link);

                    // 2. Generate summary
                    String summary = summaryService.summarize(articleText);

                    // 3. Send to Telegram
                    telegram.send("""
                            🚀 %s
                            
                            📰 %s
                            
                            ✍️ Summary:
                            %s
                            
                            🔗 %s
                            """.formatted(feedName, latest.getTitle(), summary, link));

                    // 4. Save as last seen
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

    //helper method
    private String fetch(String link) throws IOException {
        return Jsoup.connect(link).get().text();
    }
}