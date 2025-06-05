package fr.arkyan.popo.pouletmecaniquebackend.service;

import fr.arkyan.popo.pouletmecaniquebackend.data.entity.FeedProperty;

import java.util.List;

public interface IFeedPropertyService {

    List<FeedProperty> getAll();

    FeedProperty getById(Long id);

    FeedProperty save(FeedProperty feedProperty);

    void deleteById(Long id);

}
