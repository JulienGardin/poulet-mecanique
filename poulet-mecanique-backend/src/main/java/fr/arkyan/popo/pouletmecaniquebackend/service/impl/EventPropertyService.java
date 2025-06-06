package fr.arkyan.popo.pouletmecaniquebackend.service.impl;

import fr.arkyan.popo.pouletmecaniquebackend.data.entity.EventProperty;
import fr.arkyan.popo.pouletmecaniquebackend.repository.EventPropertyRepository;
import fr.arkyan.popo.pouletmecaniquebackend.service.IEventPropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventPropertyService implements IEventPropertyService {

    @Autowired
    private EventPropertyRepository eventPropertyRepository;

    @Override
    public List<EventProperty> getAll() {
        return eventPropertyRepository.findAll();
    }

    @Override
    public EventProperty save(EventProperty eventProperty) {
        return eventPropertyRepository.save(eventProperty);
    }

    @Override
    public void deleteById(Long id) {
        eventPropertyRepository.deleteById(id);
    }
}
