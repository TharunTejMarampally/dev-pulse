package com.org.devPulse.service;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.List;

@Service
public class RssReader {
    public List<SyndEntry> read(String url) throws Exception {
        SyndFeed feed = new SyndFeedInput()
                .build(new XmlReader(new URL(url)));
        return feed.getEntries();
    }
}
