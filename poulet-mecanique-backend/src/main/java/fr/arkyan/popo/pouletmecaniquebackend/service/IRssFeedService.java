package fr.arkyan.popo.pouletmecaniquebackend.service;

import com.rometools.rome.feed.synd.SyndFeed;

public interface IRssFeedService {

    SyndFeed getRssFeed(String url);

}
