package fr.arkyan.popo.pouletmecaniquebackend.service.impl;

import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import fr.arkyan.popo.pouletmecaniquebackend.exception.RssFeedException;
import fr.arkyan.popo.pouletmecaniquebackend.service.IRssFeedService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Service
public class RssFeedService implements IRssFeedService {

    @Override
    public SyndFeed getRssFeed(String url) {

        try(HttpClient client = HttpClient.newHttpClient()){
            HttpRequest request = HttpRequest.newBuilder(URI.create(url)).timeout(Duration.of(30, ChronoUnit.SECONDS)).build();
            HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());
            if (response.statusCode() != 200) {
                client.shutdownNow();
                throw new RssFeedException("Failed to fetch RSS feed (" + url + ") : HttpCode " + response.statusCode());
            }
            return new SyndFeedInput().build(new XmlReader(response.body()));
        } catch (IOException | FeedException e) {
            throw new RssFeedException(e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Restore interrupted status
            throw new RssFeedException("Interrupted while fetching RSS feed (" + url + ")", e);
        }

    }

}
