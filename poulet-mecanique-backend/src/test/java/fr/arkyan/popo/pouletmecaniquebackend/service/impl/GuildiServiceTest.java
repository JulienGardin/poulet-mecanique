package fr.arkyan.popo.pouletmecaniquebackend.service.impl;

import fr.arkyan.popo.pouletmecaniquebackend.data.dto.GuildiEvent;
import fr.arkyan.popo.pouletmecaniquebackend.service.IGuildiService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class GuildiServiceTest {

    @Autowired
    private IGuildiService guildiService;

    @Test
    public void testGetGuildiEvents() {
        List<GuildiEvent> events = guildiService.getGuildiEvents();
        assertNotNull(events);
        assertFalse(events.isEmpty());
    }

    @Test
    public void testGetGuildiCategories() {
        List<String> categories = guildiService.getGuildiCategories();
        assertNotNull(categories);
        assertFalse(categories.isEmpty());
    }

}
