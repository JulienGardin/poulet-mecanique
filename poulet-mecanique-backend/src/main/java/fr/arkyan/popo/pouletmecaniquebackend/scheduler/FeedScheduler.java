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
import java.util.HashMap;
import java.util.Map;
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

    private Map<Long, Integer> errors = new HashMap<>();

    /**
     * Removes feed older than 10 days from the database.
     * This method is scheduled to run daily at midnight.
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void removeOldFeedItemsFromDatabase() {
        feedRepository.removeOlderThan(LocalDate.now().minusDays(10));
    }

    /**
     * Scheduled method to scan RSS feeds every 15 minutes.
     * It retrieves all feed properties and processes each feed.
     * If an error occurs during processing, it logs the error and sends a message to Discord.
     */
    @Scheduled(cron = "0 */15 * * * ?")
    public void performRssScanning() {

        log.info("Starting RSS feed scanning...");

        var feeds = feedPropertyService.getAll();

        try {
            feeds.forEach(this::processFeed);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            discordApiService.sendLogErrorMessage("Error while processing feeds: " + e.getClass() + " - " + e.getMessage());
        }

    }

    /**
     * Processes each feed property by fetching the RSS feed and filtering entries.
     * @param feedProperty The feed property to process.
     */
    private void processFeed(FeedProperty feedProperty) {

        log.info("Processing feed: {}", feedProperty.getUrl());

        SyndFeed feed;
        try {
            feed = rssFeedService.getRssFeed(feedProperty.getUrl());
            errors.remove(feedProperty.getId());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            errors.put(feedProperty.getId(), errors.getOrDefault(feedProperty.getId(), 0) + 1);
            if (errors.get(feedProperty.getId()) == 4 || errors.get(feedProperty.getId()) % 12 == 0) {;
                discordApiService.sendLogErrorMessage("Error while processing feed since " + errors.get(feedProperty.getId()) + " iterations : " + feedProperty.getUrl() + "\n" + e.getClass() + " : " + e.getMessage());
            }
            return;
        }

        feed.getEntries().stream()
                .filter(this::isFromLessThan2DaysAgo)
                .filter(this::hasNotAlreadyBeenProcessed)
                .filter(this.titleMatches(feedProperty.getFilter()))
                .sorted(Comparator.comparing(SyndEntry::getPublishedDate))
                .forEach(entry -> this.processFeedEntries(feedProperty, feed, entry));

    }

    /**
     * Checks if the SyndEntry was published within the last 2 days.
     * @param syndEntry The SyndEntry to check.
     * @return true if the entry is from less than 2 days ago, false otherwise.
     */
    private boolean isFromLessThan2DaysAgo(SyndEntry syndEntry) {
        return syndEntry.getPublishedDate() != null
                && syndEntry.getPublishedDate().toInstant().isAfter(LocalDate.now().minusDays(2).atStartOfDay().toInstant(ZoneOffset.UTC));
    }

    /**
     * Checks if the SyndEntry has not already been processed.
     * @param syndEntry The SyndEntry to check.
     * @return true if the entry has not been processed, false otherwise.
     */
    private boolean hasNotAlreadyBeenProcessed(SyndEntry syndEntry) {
        return !feedRepository.existsById(syndEntry.getLink());
    }

    /**
     * Creates a predicate to filter SyndEntry based on the title.
     * If the filter is null or empty, it matches all entries.
     * @param filter The filter string to match against entry titles.
     * @return A predicate that checks if the entry title contains the filter string.
     */
    private Predicate<? super SyndEntry> titleMatches(String filter) {
        if (filter == null || filter.isBlank()) {
            return entry -> true; // No filter, match all
        }
        String lowerCaseFilter = filter.toLowerCase();
        return entry -> entry.getTitle() != null && entry.getTitle().toLowerCase().contains(lowerCaseFilter);
    }

    /**
     * Processes each SyndEntry by sending it to Discord and saving it to the database.
     * @param feedProperty The feed property associated with the entry.
     * @param feed The SyndFeed containing the entry.
     * @param entry The SyndEntry to process.
     */
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

    /**
     * Sends the SyndEntry to Discord as a message embed.
     * @param feedProperty The feed property containing Discord channel information.
     * @param feed The SyndFeed containing the entry.
     * @param entry The SyndEntry to send.
     */
    private void sendToDiscord(FeedProperty feedProperty, SyndFeed feed, SyndEntry entry) {

        MessageEmbed msg = new EmbedBuilder()
                .setTitle(entry.getTitle(), entry.getLink())
                .setAuthor(feed.getTitle(), feed.getLink(), feedProperty.getIcon())
                .setTimestamp(entry.getPublishedDate().toInstant())
                .setDescription(cleanDescription(entry.getDescription().getValue()))
                .build();

        discordApiService.sendMessage(msg, feedProperty.getDiscordChannel());

    }

    /**
     * Cleans the description by removing HTML tags and decoding HTML entities.
     * If the cleaned description exceeds 256 characters, it truncates it and appends "..." at the end.
     * @param value The description value to clean.
     * @return A cleaned CharSequence representation of the description.
     */
    private CharSequence cleanDescription(String value) {
        if (value == null) {
            return "";
        }
        // Remove HTML tags and decode HTML entities
        String cleaned = value.replaceAll("<[^>]*>", "").replace("&nbsp;", " ").trim();

        return cleaned.length() > 256 ? cleaned.substring(0, 256) + "..." : cleaned;
    }

}
