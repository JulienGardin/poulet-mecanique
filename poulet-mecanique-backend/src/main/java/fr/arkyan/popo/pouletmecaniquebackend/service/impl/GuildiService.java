package fr.arkyan.popo.pouletmecaniquebackend.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import fr.arkyan.popo.pouletmecaniquebackend.data.dto.GuildiEvent;
import fr.arkyan.popo.pouletmecaniquebackend.exception.GuildiException;
import fr.arkyan.popo.pouletmecaniquebackend.service.IGuildiService;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.net.CookieStore;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

@Service
public class GuildiService implements IGuildiService {

    @Value("${guildi.url}")
    private String guildiUrl;

    @Value("${guildi.username}")
    private String guildiUsername;

    @Value("${guildi.password}")
    private String guildiPassword;

    @Override
    public List<GuildiEvent> getGuildiEvents() {
        String authCookie = getAuthCookie();
        return getGuildiEvents(authCookie);
    }

    @Override
    public List<String> getGuildiCategories() {
        String authCookie = getAuthCookie();
        return getGuildiCategories(authCookie);
    }

    private String getAuthCookie() {

        CookieCsrf cookieCsrf = getCsrfToken();

        String postBody = String.format("userLogin=%s&userPassword=%s&phone_57d4b9cf65aa6c=&csrf_login=%s&action=login",
                guildiUsername, guildiPassword, cookieCsrf.getCsrf());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(java.net.URI.create(guildiUrl + "/fr/m/mon-compte/connexion"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Cookie", cookieCsrf.getCookie())
                .POST(HttpRequest.BodyPublishers.ofString(postBody))
                .timeout(Duration.of(30, ChronoUnit.SECONDS))
                .build();

        try (HttpClient client = HttpClient.newHttpClient()) {
            client.send(request, HttpResponse.BodyHandlers.ofString());
            return cookieCsrf.getCookie();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Restore interrupted status
            throw new GuildiException("Interrupted while retrieving auth cookie", e);
        } catch (Exception e) {
            throw new GuildiException("Failed to retrieve auth cookie", e);
        }

    }

    private CookieCsrf getCsrfToken() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(java.net.URI.create(guildiUrl + "/fr/m/mon-compte/connexion"))
                .timeout(Duration.of(30, ChronoUnit.SECONDS))
                .build();
        try(HttpClient client = HttpClient.newHttpClient()){
            HttpResponse<String> response =  client.send(request, HttpResponse.BodyHandlers.ofString());

            // Extract Cookie
            String cookie = response.headers().allValues("Set-Cookie").stream()
                    .filter(header -> header.startsWith("PHPSESSID="))
                    .findFirst()
                    .orElseThrow(() -> new GuildiException("No Set-Cookie header found"))
                    .split(";")[0];

            // Extract CSRF token
            Document doc = Jsoup.parse(response.body());
            String csrf = doc.select("input[name=csrf_login]").val();

            return new CookieCsrf(csrf, cookie);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Restore interrupted status
            throw new GuildiException("Interrupted while retrieving CSRF token", e);
        } catch (Exception e) {
            throw new GuildiException("Failed to retrieve CSRF token", e);
        }
    }

    private List<GuildiEvent> getGuildiEvents(String authCookie) {

        String startDate = LocalDate.now().toString();
        String endDate = LocalDate.now().plusYears(1).toString();

        String url = String.format("%s/ajax/fr/?q=eventList&m=RaidPlanner&categoryID=&start=%sT00:00:00+02:00&end=%sT00:00:00+02:00",
                guildiUrl, startDate, endDate);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(java.net.URI.create(url))
                .header("Cookie", authCookie)
                .timeout(Duration.of(30, ChronoUnit.SECONDS))
                .build();

        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String body = response.body();
            ObjectMapper mapper = JsonMapper.builder()
                    .findAndAddModules()
                    .build();
            return mapper.readValue(body, new ObjectMapper().getTypeFactory().constructCollectionType(List.class, GuildiEvent.class));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Restore interrupted status
            throw new GuildiException("Interrupted while retrieving Guildi events", e);
        } catch (Exception e) {
            throw new GuildiException("Failed to retrieve Guildi events", e);
        }

    }

    private List<String> getGuildiCategories(String authCookie) {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(java.net.URI.create(guildiUrl + "/fr/m/raidplanner"))
                .header("Cookie", authCookie)
                .timeout(Duration.of(30, ChronoUnit.SECONDS))
                .build();

        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            Document doc = Jsoup.parse(response.body());
            Element divCategories = doc.selectFirst("div.raid-categories");

            if (divCategories == null) {
                throw new GuildiException("No categories found in the response");
            }

            return divCategories.select("a").stream()
                    .map(elmt -> elmt.attr("href"))
                    .filter(StringUtils::hasText)
                    .map(url -> url.substring(url.lastIndexOf("/") + 1))
                    .toList();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Restore interrupted status
            throw new GuildiException("Interrupted while retrieving Guildi categories", e);
        } catch (Exception e) {
            throw new GuildiException("Failed to retrieve Guildi categories", e);
        }

    }

    @Data
    @AllArgsConstructor
    private static class CookieCsrf {
        private String csrf;
        private String cookie;
    }

}
