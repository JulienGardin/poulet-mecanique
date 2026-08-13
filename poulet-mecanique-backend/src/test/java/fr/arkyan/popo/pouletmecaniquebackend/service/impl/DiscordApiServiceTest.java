package fr.arkyan.popo.pouletmecaniquebackend.service.impl;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.*;

@SpringBootTest
class DiscordApiServiceTest {

    @Autowired
    private DiscordApiService discordApiService;

    @Test
    void getTextChannels_shouldReturnNonEmptyList() {
        var textChannels = discordApiService.getTextChannels();
        Assertions.assertNotNull(textChannels, "Text channels should not be null");
        Assertions.assertFalse(textChannels.isEmpty(), "Text channels list should not be empty");
    }

    @Test
    void sendLogErrorMessage_shouldNotThrowException() {
        String testMessage = "This is a test error message";
        Assertions.assertDoesNotThrow(() -> discordApiService.sendLogErrorMessage(testMessage), "Sending log error message should not throw an exception");
    }

    @Test
    void sendMessage_shouldNotThrowException() {

        ZoneId zone = ZoneId.of("Europe/Paris");
        ZoneOffset zoneOffSet = zone.getRules().getOffset(LocalDateTime.now());

        MessageEmbed msg = new EmbedBuilder()
                .setTitle("TEST TITLE")
                .setAuthor("Author")
                .setTimestamp(LocalDateTime.of(LocalDate.now(), LocalTime.of(18,0,0)).toInstant(zoneOffSet))
                .setDescription("<@&1400931031997087744> Nouvel évènement programmé. N'oubliez pas de vous inscrire !")
                .build();

        Assertions.assertDoesNotThrow(() -> discordApiService.sendMessage(msg, "494903810137784323"), "Sending message should not throw an exception");
    }

    @Test
    void getRoles_shouldReturnNonEmptyList() {
        var roles = discordApiService.getMentionnableRoles();
        Assertions.assertNotNull(roles, "Roles should not be null");
        Assertions.assertFalse(roles.isEmpty(), "Roles list should not be empty");
    }

}
