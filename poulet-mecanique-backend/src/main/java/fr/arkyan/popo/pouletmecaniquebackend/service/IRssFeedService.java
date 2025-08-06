package fr.arkyan.popo.pouletmecaniquebackend.service;

import com.rometools.rome.feed.synd.SyndFeed;

public interface IRssFeedService {

    /**
     * Retrieves an RSS feed from the specified URL.
     * @param url the URL of the RSS feed
     * @return the SyndFeed object representing the RSS feed
     */
    SyndFeed getRssFeed(String url);

}
