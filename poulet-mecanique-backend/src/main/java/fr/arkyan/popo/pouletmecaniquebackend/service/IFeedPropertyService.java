package fr.arkyan.popo.pouletmecaniquebackend.service;

import fr.arkyan.popo.pouletmecaniquebackend.data.entity.FeedProperty;

import java.util.List;

public interface IFeedPropertyService {

    /**
     * Retrieves all feed properties.
     * @return a list of all feed properties
     */
    List<FeedProperty> getAll();

    /**
     * Saves a feed property.
     * @param feedProperty the feed property to save
     * @return the saved feed property
     */
    FeedProperty save(FeedProperty feedProperty);

    /**
     * Deletes a feed property by its ID.
     * @param id the ID of the feed property to delete
     */
    void deleteById(Long id);

}
