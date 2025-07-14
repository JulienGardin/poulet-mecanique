package fr.arkyan.popo.pouletmecaniquebackend.scheduler;

import fr.arkyan.popo.pouletmecaniquebackend.data.dto.GuildiEvent;
import fr.arkyan.popo.pouletmecaniquebackend.data.entity.Event;
import fr.arkyan.popo.pouletmecaniquebackend.data.entity.EventProperty;
import fr.arkyan.popo.pouletmecaniquebackend.repository.EventRepository;
import fr.arkyan.popo.pouletmecaniquebackend.service.IDiscordApiService;
import fr.arkyan.popo.pouletmecaniquebackend.service.IEventPropertyService;
import fr.arkyan.popo.pouletmecaniquebackend.service.IGuildiService;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class EventScheduler {

    @Value("${guildi.url}")
    private String guildiUrl;

    @Value("${guildi.icon}")
    private String guildiIcon;

    @Value("${guildi.thumbnail}")
    private String guildiThumbnail;

    @Autowired
    private IEventPropertyService eventPropertyService;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private IGuildiService guildiService;

    @Autowired
    private IDiscordApiService discordApiService;

    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Scheduled(cron = "0 0 0 * * *")
    public void removeOldEventItemsFromDatabase() {
        eventRepository.removeOlderThan(LocalDate.now().minusDays(10));
    }

    @Scheduled(cron = "0 */15 * * * ?")
    public void performEventScanning() {

        log.info("Starting Guildi Events scanning...");

        try {
            var events = guildiService.getGuildiEvents();
            var eventProperties = eventPropertyService.getAll();
            events.forEach(e -> this.processEvent(e, eventProperties));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            discordApiService.sendLogErrorMessage("Error while processing events: " + e.getMessage());
        }

    }

    private void processEvent(GuildiEvent event, List<EventProperty> eventProperties) {
        if(!eventRepository.existsById(event.getUrl())){
            // New event
            log.info("New event found: {}", event.getTitle());
            Optional<EventProperty> property = eventProperties.stream()
                    .filter(ep -> ep.getCategory().equals(event.getCategory()))
                    .findFirst();
            if(property.isPresent()){
                sendToDiscord(property.get(), event, false);
                Event dbEvent = new Event(event.getUrl(), event.getStart().toLocalDate());
                long remaining = LocalDateTime.now().until(event.getStart(), ChronoUnit.HOURS);
                if(remaining < 24){
                    dbEvent.setReminder24hDone(true);
                }
                if(remaining < 3){
                    dbEvent.setReminder3hDone(true);
                }
                eventRepository.save(dbEvent);
            }else{
                log.warn("No event property found for category: {}", event.getCategory());
            }

        }else{
            // old event, check for reminder
            Optional<EventProperty> property = eventProperties.stream()
                    .filter(ep -> ep.getCategory().equals(event.getCategory()))
                    .findFirst();

            if(property.isEmpty()){
                log.warn("No event property found for category: {}", event.getCategory());
                return;
            }

            Event dbEvent = eventRepository.findById(event.getUrl()).orElseThrow();
            long remaining = LocalDateTime.now().until(event.getStart(), ChronoUnit.HOURS);
            if(!dbEvent.isReminder24hDone() && remaining < 24 && remaining >= 3) {
                log.info("Sending 24h reminder for event: {}", event.getTitle());
                sendToDiscord(property.get(), event, true);
                dbEvent.setReminder24hDone(true);
                eventRepository.save(dbEvent);
            }
            if(!dbEvent.isReminder3hDone() && remaining < 3) {
                log.info("Sending 3h reminder for event: {}", event.getTitle());
                sendToDiscord(property.get(), event, true);
                dbEvent.setReminder3hDone(true);
                eventRepository.save(dbEvent);
            }
        }
    }

    private void sendToDiscord(EventProperty eventProperty, GuildiEvent event, boolean reminder) {

        MessageEmbed msg = new EmbedBuilder()
                .setTitle(event.getTitle(), event.getUrl())
                .setAuthor("Calendrier | " + eventProperty.getLabel(), guildiUrl, guildiIcon)
                .setTimestamp(event.getStart())
                .setColor(parseColor(event.getColor()))
                .setThumbnail(guildiThumbnail)
                .setDescription(reminder ? "Un évènement est proche. N'oubliez pas de vous inscrire si ce n'est déjà fait !"
                        : "Nouvel évènement programmé. N'oubliez pas de vous inscrire !")
                .build();

        discordApiService.sendMessage(msg, eventProperty.getDiscordChannel());

    }

    public static Color parseColor(String input) {
        Pattern c = Pattern.compile("rgb *\\( *([\\d]+), *([\\d]+), *([\\d]+) *\\)");
        Matcher m = c.matcher(input);

        if (m.matches()) {
            return new Color(Integer.parseInt(m.group(1)),
                    Integer.parseInt(m.group(2)),
                    Integer.parseInt(m.group(3)));
        }
        return null;
    }

}
