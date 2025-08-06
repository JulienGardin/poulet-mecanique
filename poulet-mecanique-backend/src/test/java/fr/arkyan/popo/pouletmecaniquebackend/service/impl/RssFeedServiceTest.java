package fr.arkyan.popo.pouletmecaniquebackend.service.impl;

import com.rometools.rome.feed.synd.SyndFeed;
import fr.arkyan.popo.pouletmecaniquebackend.exception.RssFeedException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class RssFeedServiceTest {

    @Autowired
    private RssFeedService rssFeedService;

    @Test
    void getRssFeed_shouldThrowException_whenUrlIsInvalid() {
        String invalidUrl = "http://invalid-url";
        Executable executable = () -> rssFeedService.getRssFeed(invalidUrl);
        Assertions.assertThrows(RssFeedException.class, executable);
    }

    @Test
    void getRssFeed_shouldReturnFeed_whenUrlIsValid() {
        String validUrl = "https://www.lemonde.fr/rss/une.xml";
        SyndFeed feed = rssFeedService.getRssFeed(validUrl);
        Assertions.assertNotNull(feed);
        Assertions.assertNotNull(feed.getTitle());
    }
}

