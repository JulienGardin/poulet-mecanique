package fr.arkyan.popo.pouletmecaniquebackend.service.impl;

import fr.arkyan.popo.pouletmecaniquebackend.data.entity.FeedProperty;
import fr.arkyan.popo.pouletmecaniquebackend.repository.FeedPropertyRepository;
import fr.arkyan.popo.pouletmecaniquebackend.service.IFeedPropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeedPropertyService implements IFeedPropertyService {

    @Autowired
    private FeedPropertyRepository feedPropertyRepository;

    @Override
    public List<FeedProperty> getAll() {
        return feedPropertyRepository.findAll();
    }

    @Override
    public FeedProperty save(FeedProperty feedProperty) {
        return feedPropertyRepository.save(feedProperty);
    }

    @Override
    public void deleteById(Long id) {
        feedPropertyRepository.deleteById(id);
    }
}
