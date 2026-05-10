package com.org.devPulse.service;

import com.rometools.rome.feed.synd.SyndEntry;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class ReleaseScheduler {
    private final RssReader reader;
    private final ReleaseFilter filter;
    private final TelegramService telegram;

    @Scheduled(fixedRate = 600_000)
    public void checkReleases() throws Exception {

        Map<String, String> feeds = Map.of(
                "Spring Boot", "https://github.com/spring-projects/spring-boot/releases.atom",
                "Spring Framework", "https://github.com/spring-projects/spring-framework/releases.atom"
        );

        for (var feed : feeds.entrySet()) {
            for (SyndEntry e : reader.read(feed.getValue())) {
                if (filter.isNew(e)) {
                    telegram.send(
                            "🚀 [" + feed.getKey() + "] New Release\n\n"
                                    + e.getTitle() + "\n" + e.getLink()
                    );
                }
            }
        }
    }
}
