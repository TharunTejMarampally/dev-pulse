package com.org.devPulse.service;

import com.org.devPulse.entity.Feed;
import com.org.devPulse.repository.FeedRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeedService {
    private final FeedRepository feedRepository;

    public FeedService(FeedRepository feedRepository) {
        this.feedRepository = feedRepository;
    }

    public List<Feed> getActiveFeeds() {
        return feedRepository.findByActiveTrue();
    }

    public List<Feed> saveFeed(List<Feed> feeds) {
        for (Feed request : feeds) {
            if (request.getFeedName() == null || request.getRssUrl() == null) {
                throw new IllegalArgumentException("Feed name and RSS URL cannot be null");
            }
        }
        return feedRepository.saveAll(feeds);
    }
}
