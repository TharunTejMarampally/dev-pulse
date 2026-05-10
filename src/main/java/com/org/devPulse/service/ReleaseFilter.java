package com.org.devPulse.service;


import com.rometools.rome.feed.synd.SyndEntry;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ReleaseFilter {
    private final Set<String> seen = ConcurrentHashMap.newKeySet();

    public boolean isNew(SyndEntry entry) {
        return seen.add(entry.getLink());
    }
}
