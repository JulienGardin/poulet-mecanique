package fr.arkyan.popo.pouletmecaniquebackend.service.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
    void getCategories_shouldReturnNonEmptyList() {
        var categories = discordApiService.getCategories();
        Assertions.assertNotNull(categories, "Categories should not be null");
        Assertions.assertFalse(categories.isEmpty(), "Categories list should not be empty");
    }

    @Test
    void sendLogErrorMessage_shouldNotThrowException() {
        String testMessage = "This is a test error message";
        Assertions.assertDoesNotThrow(() -> discordApiService.sendLogErrorMessage(testMessage), "Sending log error message should not throw an exception");
    }

}
