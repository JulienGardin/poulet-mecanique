package fr.arkyan.popo.pouletmecaniquebackend.service;

import fr.arkyan.popo.pouletmecaniquebackend.data.entity.EventProperty;

import java.util.List;

public interface IEventPropertyService {

    /**
     * Get all event properties.
     * @return a list of all event properties
     */
    List<EventProperty> getAll();

    /**
     * Save an event property.
     * @param eventProperty the event property to save
     * @return the saved event property
     */
    EventProperty save(EventProperty eventProperty);

    /**
     * Delete an event property by its ID.
     * @param id the ID of the event property to delete
     */
    void deleteById(Long id);

}
