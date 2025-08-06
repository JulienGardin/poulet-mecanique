package fr.arkyan.popo.pouletmecaniquebackend.scheduler;

import fr.arkyan.popo.pouletmecaniquebackend.data.dto.GuildiEvent;
import fr.arkyan.popo.pouletmecaniquebackend.data.entity.Event;
import fr.arkyan.popo.pouletmecaniquebackend.data.entity.EventProperty;
import fr.arkyan.popo.pouletmecaniquebackend.repository.EventRepository;
import fr.arkyan.popo.pouletmecaniquebackend.service.IDiscordApiService;
import fr.arkyan.popo.pouletmecaniquebackend.service.IEventPropertyService;
import fr.arkyan.popo.pouletmecaniquebackend.service.IGuildiService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@SpringBootTest
public class EventSchedulerTests {

    @Mock
    private IEventPropertyService eventPropertyService;

    @Mock
    private IGuildiService guildiService;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private IDiscordApiService discordApiService;

    @InjectMocks
    private EventScheduler eventScheduler;

    @Test
    public void testShouldSendNewEventMessage() {

        Mockito.when(eventPropertyService.getAll()).thenReturn(List.of(
                new EventProperty(1L, "Test Event Property", "Category101", "123456789012345678")
        ));
        Mockito.when(guildiService.getGuildiEvents()).thenReturn(List.of(
                new GuildiEvent("1", "rgb(100,150,200)", LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(2).plusHours(3),
                        "rgb(110,160,210)", "Test Event", "Category101", "https://example.com/raidplanner/Category101/1")
        ));
        Mockito.when(eventRepository.existsById(Mockito.any())).thenReturn(false);
        Mockito.when(eventRepository.save(Mockito.any())).thenReturn(new Event());

        eventScheduler.performEventScanning();

        Mockito.verify(eventPropertyService, Mockito.times(1)).getAll();
        Mockito.verifyNoMoreInteractions(eventPropertyService);

        Mockito.verify(guildiService, Mockito.times(1)).getGuildiEvents();
        Mockito.verifyNoMoreInteractions(guildiService);

        Mockito.verify(eventRepository, Mockito.times(1)).save(Mockito.argThat((event) ->
                "https://example.com/raidplanner/Category101/1".equals(event.getUrl())
                        && !event.isReminder24hDone() && !event.isReminder3hDone()
        ));

        Mockito.verify(discordApiService, Mockito.times(1)).sendMessage(Mockito.argThat((message) ->
                "Test Event".equals(message.getTitle())
                        && Objects.equals(message.getColor(), new java.awt.Color(100, 150, 200))
        ), Mockito.eq("123456789012345678"));

    }

    @Test
    public void testShouldSendNewEventWith24hReminderEventMessage() {

        Mockito.when(eventPropertyService.getAll()).thenReturn(List.of(
                new EventProperty(1L, "Test Event Property", "Category101", "123456789012345678")
        ));
        Mockito.when(guildiService.getGuildiEvents()).thenReturn(List.of(
                new GuildiEvent("1", "rgb(100,150,200)", LocalDateTime.now().plusDays(1).minusHours(4), LocalDateTime.now().plusDays(1).minusHours(1),
                        "rgb(110,160,210)", "Test Event", "Category101", "https://example.com/raidplanner/Category101/1")
        ));
        Mockito.when(eventRepository.existsById(Mockito.any())).thenReturn(false);
        Mockito.when(eventRepository.save(Mockito.any())).thenReturn(new Event());

        eventScheduler.performEventScanning();

        Mockito.verify(eventPropertyService, Mockito.times(1)).getAll();
        Mockito.verifyNoMoreInteractions(eventPropertyService);

        Mockito.verify(guildiService, Mockito.times(1)).getGuildiEvents();
        Mockito.verifyNoMoreInteractions(guildiService);

        Mockito.verify(eventRepository, Mockito.times(1)).save(Mockito.argThat((event) ->
                "https://example.com/raidplanner/Category101/1".equals(event.getUrl())
                        && event.isReminder24hDone() && !event.isReminder3hDone()
        ));

        Mockito.verify(discordApiService, Mockito.times(1)).sendMessage(Mockito.argThat((message) ->
                "Test Event".equals(message.getTitle())
                        && Objects.equals(message.getColor(), new java.awt.Color(100, 150, 200))
        ), Mockito.eq("123456789012345678"));

    }

    @Test
    public void testShouldSendNewEventWith24hAnd3hRemindersEventMessage() {

        Mockito.when(eventPropertyService.getAll()).thenReturn(List.of(
                new EventProperty(1L, "Test Event Property", "Category101", "123456789012345678")
        ));
        Mockito.when(guildiService.getGuildiEvents()).thenReturn(List.of(
                new GuildiEvent("1", "rgb(100,150,200)", LocalDateTime.now().plusHours(2), LocalDateTime.now().plusHours(5),
                        "rgb(110,160,210)", "Test Event", "Category101", "https://example.com/raidplanner/Category101/1")
        ));
        Mockito.when(eventRepository.existsById(Mockito.any())).thenReturn(false);
        Mockito.when(eventRepository.save(Mockito.any())).thenReturn(new Event());

        eventScheduler.performEventScanning();

        Mockito.verify(eventPropertyService, Mockito.times(1)).getAll();
        Mockito.verifyNoMoreInteractions(eventPropertyService);

        Mockito.verify(guildiService, Mockito.times(1)).getGuildiEvents();
        Mockito.verifyNoMoreInteractions(guildiService);

        Mockito.verify(eventRepository, Mockito.times(1)).save(Mockito.argThat((event) ->
                "https://example.com/raidplanner/Category101/1".equals(event.getUrl())
                        && event.isReminder24hDone() && event.isReminder3hDone()
        ));

        Mockito.verify(discordApiService, Mockito.times(1)).sendMessage(Mockito.argThat((message) ->
                "Test Event".equals(message.getTitle())
                        && Objects.equals(message.getColor(), new java.awt.Color(100, 150, 200))
        ), Mockito.eq("123456789012345678"));

    }

    @Test
    public void testShouldSend24hRemindersEventMessage() {

        Mockito.when(eventPropertyService.getAll()).thenReturn(List.of(
                new EventProperty(1L, "Test Event Property", "Category101", "123456789012345678")
        ));
        Mockito.when(guildiService.getGuildiEvents()).thenReturn(List.of(
                new GuildiEvent("1", "rgb(100,150,200)", LocalDateTime.now().plusHours(23), LocalDateTime.now().plusHours(26),
                        "rgb(110,160,210)", "Test Event", "Category101", "https://example.com/raidplanner/Category101/1")
        ));
        Mockito.when(eventRepository.existsById(Mockito.any())).thenReturn(true);
        Mockito.when(eventRepository.findById(Mockito.any())).thenReturn(Optional.of(new Event("https://example.com/raidplanner/Category101/1",
                LocalDateTime.now().plusHours(23).toLocalDate(), false, false)));
        Mockito.when(eventRepository.save(Mockito.any())).thenReturn(new Event());

        eventScheduler.performEventScanning();

        Mockito.verify(eventPropertyService, Mockito.times(1)).getAll();
        Mockito.verifyNoMoreInteractions(eventPropertyService);

        Mockito.verify(guildiService, Mockito.times(1)).getGuildiEvents();
        Mockito.verifyNoMoreInteractions(guildiService);

        Mockito.verify(eventRepository, Mockito.times(1)).save(Mockito.argThat((event) ->
                "https://example.com/raidplanner/Category101/1".equals(event.getUrl())
                        && event.isReminder24hDone() && !event.isReminder3hDone()
        ));

        Mockito.verify(discordApiService, Mockito.times(1)).sendMessage(Mockito.argThat((message) ->
                "Test Event - J-1".equals(message.getTitle())
                        && Objects.equals(message.getColor(), new java.awt.Color(100, 150, 200))
        ), Mockito.eq("123456789012345678"));

    }

    @Test
    public void testShouldSend3hRemindersEventMessage() {

        Mockito.when(eventPropertyService.getAll()).thenReturn(List.of(
                new EventProperty(1L, "Test Event Property", "Category101", "123456789012345678")
        ));
        Mockito.when(guildiService.getGuildiEvents()).thenReturn(List.of(
                new GuildiEvent("1", "rgb(100,150,200)", LocalDateTime.now().plusHours(2), LocalDateTime.now().plusHours(5),
                        "rgb(110,160,210)", "Test Event", "Category101", "https://example.com/raidplanner/Category101/1")
        ));
        Mockito.when(eventRepository.existsById(Mockito.any())).thenReturn(true);
        Mockito.when(eventRepository.findById(Mockito.any())).thenReturn(Optional.of(new Event("https://example.com/raidplanner/Category101/1",
                LocalDateTime.now().plusHours(23).toLocalDate(), false, false)));
        Mockito.when(eventRepository.save(Mockito.any())).thenReturn(new Event());

        eventScheduler.performEventScanning();

        Mockito.verify(eventPropertyService, Mockito.times(1)).getAll();
        Mockito.verifyNoMoreInteractions(eventPropertyService);

        Mockito.verify(guildiService, Mockito.times(1)).getGuildiEvents();
        Mockito.verifyNoMoreInteractions(guildiService);

        Mockito.verify(eventRepository, Mockito.times(1)).save(Mockito.argThat((event) ->
                "https://example.com/raidplanner/Category101/1".equals(event.getUrl())
                        && event.isReminder24hDone() && event.isReminder3hDone()
        ));

        Mockito.verify(discordApiService, Mockito.times(1)).sendMessage(Mockito.argThat((message) ->
                "Test Event - H-3".equals(message.getTitle())
                        && Objects.equals(message.getColor(), new java.awt.Color(100, 150, 200))
        ), Mockito.eq("123456789012345678"));

    }

    @Test
    public void testShouldNotSendEventMessage() {

        Mockito.when(eventPropertyService.getAll()).thenReturn(List.of(
                new EventProperty(1L, "Test Event Property", "Category101", "123456789012345678")
        ));
        Mockito.when(guildiService.getGuildiEvents()).thenReturn(List.of(
                new GuildiEvent("1", "rgb(100,150,200)", LocalDateTime.now().plusDays(3), LocalDateTime.now().plusDays(3).plusHours(3),
                        "rgb(110,160,210)", "Test Event", "Category101", "https://example.com/raidplanner/Category101/1")
        ));
        Mockito.when(eventRepository.existsById(Mockito.any())).thenReturn(true);
        Mockito.when(eventRepository.findById(Mockito.any())).thenReturn(Optional.of(new Event("https://example.com/raidplanner/Category101/1",
                LocalDateTime.now().plusDays(3).toLocalDate(), false, false)));

        eventScheduler.performEventScanning();

        Mockito.verify(eventPropertyService, Mockito.times(1)).getAll();
        Mockito.verifyNoMoreInteractions(eventPropertyService);

        Mockito.verify(guildiService, Mockito.times(1)).getGuildiEvents();
        Mockito.verifyNoMoreInteractions(guildiService);

        Mockito.verifyNoInteractions(discordApiService);

    }

    @Test
    public void testShouldNotSendEventReminderMessage() {

        Mockito.when(eventPropertyService.getAll()).thenReturn(List.of(
                new EventProperty(1L, "Test Event Property", "Category101", "123456789012345678")
        ));
        Mockito.when(guildiService.getGuildiEvents()).thenReturn(List.of(
                new GuildiEvent("1", "rgb(100,150,200)", LocalDateTime.now().plusHours(6), LocalDateTime.now().plusHours(9),
                        "rgb(110,160,210)", "Test Event", "Category101", "https://example.com/raidplanner/Category101/1")
        ));
        Mockito.when(eventRepository.existsById(Mockito.any())).thenReturn(true);
        Mockito.when(eventRepository.findById(Mockito.any())).thenReturn(Optional.of(new Event("https://example.com/raidplanner/Category101/1",
                LocalDateTime.now().plusDays(3).toLocalDate(), true, false)));

        eventScheduler.performEventScanning();

        Mockito.verify(eventPropertyService, Mockito.times(1)).getAll();
        Mockito.verifyNoMoreInteractions(eventPropertyService);

        Mockito.verify(guildiService, Mockito.times(1)).getGuildiEvents();
        Mockito.verifyNoMoreInteractions(guildiService);

        Mockito.verifyNoInteractions(discordApiService);

    }

}
