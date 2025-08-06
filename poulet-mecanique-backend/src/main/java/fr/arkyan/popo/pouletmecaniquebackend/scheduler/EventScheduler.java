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
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class EventScheduler {

    protected enum EventType {

        NEW, REMINDER_24H, REMINDER_3H;

        String titleAppend() {
            return switch (this) {
                case NEW -> "";
                case REMINDER_24H -> " - J-1";
                case REMINDER_3H -> " - H-3";
            };
        }

        String description() {
            return switch (this) {
                case NEW -> "Nouvel évènement programmé. N'oubliez pas de vous inscrire !";
                case REMINDER_24H -> "Un évènement est proche. N'oubliez pas de vous inscrire si ce n'est déjà fait !";
                case REMINDER_3H -> "Un évènement est imminent. N'oubliez pas de vous inscrire si ce n'est déjà fait !";
            };
        }

    }

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

    /**
     * Removes events older than 10 days from the database.
     * This method is scheduled to run daily at midnight.
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void removeOldEventItemsFromDatabase() {
        eventRepository.removeOlderThan(LocalDate.now().minusDays(10));
    }

    /**
     * Scans for new Guildi events every 15 minutes.
     * It retrieves events from the Guildi service and processes them.
     * If an error occurs during processing, it logs the error and sends a message to Discord.
     */
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

        log.info("End Guildi Events scanning...");

    }

    /**
     * Processes a GuildiEvent by checking if it is new or old and sending reminders if necessary.
     * @param event the GuildiEvent to process
     * @param eventProperties the list of EventProperty to find the corresponding Discord channel
     */
    private void processEvent(GuildiEvent event, List<EventProperty> eventProperties) {
        if(!eventRepository.existsById(event.getUrl())){
            // New event
            log.info("New event found: {}", event.getTitle());
            Optional<EventProperty> property = eventProperties.stream()
                    .filter(ep -> ep.getCategory().equals(event.getCategory()))
                    .findFirst();
            if(property.isPresent()){
                sendToDiscord(property.get(), event, EventType.NEW);
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
                sendToDiscord(property.get(), event, EventType.REMINDER_24H);
                dbEvent.setReminder24hDone(true);
                eventRepository.save(dbEvent);
            }
            if(!dbEvent.isReminder3hDone() && remaining < 3) {
                log.info("Sending 3h reminder for event: {}", event.getTitle());
                sendToDiscord(property.get(), event, EventType.REMINDER_3H);
                dbEvent.setReminder24hDone(true);
                dbEvent.setReminder3hDone(true);
                eventRepository.save(dbEvent);
            }
        }
    }

    /**
     * Sends a message to Discord with event details.
     * @param eventProperty the event property containing Discord channel information
     * @param event the event to be sent
     * @param eventType the type of event (new or reminder)
     */
    private void sendToDiscord(EventProperty eventProperty, GuildiEvent event, EventType eventType) {

        ZoneId zone = ZoneId.of("Europe/Paris");
        ZoneOffset zoneOffSet = zone.getRules().getOffset(LocalDateTime.now());

        MessageEmbed msg = new EmbedBuilder()
                .setTitle(event.getTitle() + eventType.titleAppend(), event.getUrl())
                .setAuthor("Calendrier | " + eventProperty.getLabel(), guildiUrl, guildiIcon)
                .setTimestamp(event.getStart().toInstant(zoneOffSet))
                .setColor(parseColor(event.getColor()))
                .setThumbnail(guildiThumbnail)
                .setDescription(eventType.description())
                .build();

        discordApiService.sendMessage(msg, eventProperty.getDiscordChannel());

    }

    /**
     * Parses a color string in the format "rgb(r, g, b)" and returns a Color object.
     * If the input does not match the expected format, it returns null.
     * @param input the color string to parse
     * @return a Color object or null if the input is invalid
     */
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
