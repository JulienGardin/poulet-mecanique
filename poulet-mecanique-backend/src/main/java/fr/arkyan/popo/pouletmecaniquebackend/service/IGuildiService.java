package fr.arkyan.popo.pouletmecaniquebackend.service;

import fr.arkyan.popo.pouletmecaniquebackend.data.dto.GuildiEvent;

import java.util.List;

public interface IGuildiService {

    /**
     * Retrieves all Guildi events.
     * @return a list of Guildi events
     */
    List<GuildiEvent> getGuildiEvents();

    /**
     * Retrieves all Guildi categories.
     * @return a list of Guildi categories
     */
    List<String> getGuildiCategories();

}
