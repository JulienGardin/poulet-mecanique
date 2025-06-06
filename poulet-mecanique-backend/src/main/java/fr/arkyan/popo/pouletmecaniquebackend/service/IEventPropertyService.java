package fr.arkyan.popo.pouletmecaniquebackend.service;

import fr.arkyan.popo.pouletmecaniquebackend.data.entity.EventProperty;

import java.util.List;

public interface IEventPropertyService {

    List<EventProperty> getAll();

    EventProperty save(EventProperty feedProperty);

    void deleteById(Long id);

}
