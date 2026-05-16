package com.org.devPulse.controller;


import com.org.devPulse.entity.Feed;
import com.org.devPulse.service.FeedService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class FeedController {

    private final FeedService feedService;

    public FeedController(FeedService feedService) {
        this.feedService = feedService;
    }

    @GetMapping("/feeds")
    public List<Feed> getActiveFeeds() {
        return feedService.getActiveFeeds();
    }

    @PostMapping("/feeds")
    public List<Feed> saveFeed(@RequestBody List<Feed> feeds) {
        return feedService.saveFeed(feeds);
    }
}
