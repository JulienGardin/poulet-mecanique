package fr.arkyan.popo.pouletmecaniquebackend.scheduler;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import fr.arkyan.popo.pouletmecaniquebackend.data.entity.Feed;
import fr.arkyan.popo.pouletmecaniquebackend.data.entity.FeedProperty;
import fr.arkyan.popo.pouletmecaniquebackend.repository.FeedRepository;
import fr.arkyan.popo.pouletmecaniquebackend.service.IDiscordApiService;
import fr.arkyan.popo.pouletmecaniquebackend.service.IFeedPropertyService;
import fr.arkyan.popo.pouletmecaniquebackend.service.IRssFeedService;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.function.Predicate;

@Slf4j
@Component
public class FeedScheduler {

    @Autowired
    private IFeedPropertyService feedPropertyService;

    @Autowired
    private FeedRepository feedRepository;

    @Autowired
    private IRssFeedService rssFeedService;

    @Autowired
    private IDiscordApiService discordApiService;

    @Scheduled(cron = "0 0 0 * * *")
    public void removeOldFeedItemsFromDatabase() {
        feedRepository.removeOlderThan(LocalDate.now().minusDays(10));
    }

    @Scheduled(cron = "0 */15 * * * ?")
    public void performRssScanning() {

        log.info("Starting RSS feed scanning...");

        var feeds = feedPropertyService.getAll();

        try {
            feeds.forEach(this::processFeed);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            discordApiService.sendLogErrorMessage("Error while processing feeds: " + e.getMessage());
        }

    }

    private void processFeed(FeedProperty feedProperty) {

        log.info("Processing feed: {}", feedProperty.getUrl());

        SyndFeed feed;
        try {
            feed = rssFeedService.getRssFeed(feedProperty.getUrl());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            discordApiService.sendLogErrorMessage("Error while processing feed: " + feedProperty.getUrl() + "\n" + e.getMessage());
            return;
        }

        feed.getEntries().stream()
                .filter(this::isFromLessThan2DaysAgo)
                .filter(this::hasNotAlreadyBeenProcessed)
                .filter(this.titleMatches(feedProperty.getFilter()))
                .sorted(Comparator.comparing(SyndEntry::getPublishedDate))
                .forEach(entry -> this.processFeedEntries(feedProperty, feed, entry));

    }

    private boolean isFromLessThan2DaysAgo(SyndEntry syndEntry) {
        return syndEntry.getPublishedDate() != null
                && syndEntry.getPublishedDate().toInstant().isAfter(LocalDate.now().minusDays(2).atStartOfDay().toInstant(ZoneOffset.UTC));
    }

    private boolean hasNotAlreadyBeenProcessed(SyndEntry syndEntry) {
        return !feedRepository.existsById(syndEntry.getLink());
    }

    private Predicate<? super SyndEntry> titleMatches(String filter) {
        if (filter == null || filter.isBlank()) {
            return entry -> true; // No filter, match all
        }
        String lowerCaseFilter = filter.toLowerCase();
        return entry -> entry.getTitle() != null && entry.getTitle().toLowerCase().contains(lowerCaseFilter);
    }

    private void processFeedEntries(FeedProperty feedProperty, SyndFeed feed, SyndEntry entry) {

        try {
            sendToDiscord(feedProperty, feed, entry);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            discordApiService.sendLogErrorMessage("Error while processing feed entry : " + entry.getLink() + "\n" + e.getMessage());
            return;
        }

        feedRepository.save(new Feed(entry.getLink()));

    }

    private void sendToDiscord(FeedProperty feedProperty, SyndFeed feed, SyndEntry entry) {

        MessageEmbed msg = new EmbedBuilder()
                .setTitle(entry.getTitle(), entry.getLink())
                .setAuthor(feed.getTitle(), feed.getLink(), feedProperty.getIcon())
                .setTimestamp(entry.getPublishedDate().toInstant())
                .setDescription(cleanDescription(entry.getDescription().getValue()))
                .build();

        discordApiService.sendMessage(msg, feedProperty.getDiscordChannel());

    }

    private CharSequence cleanDescription(String value) {
        if (value == null) {
            return "";
        }
        // Remove HTML tags and decode HTML entities
        String cleaned = value.replaceAll("<[^>]*>", "").replace("&nbsp;", " ").trim();

        return cleaned.length() > 256 ? cleaned.substring(0, 256) + "..." : cleaned;
    }

}
