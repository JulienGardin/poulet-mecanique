package fr.arkyan.popo.pouletmecaniquebackend.service;

import fr.arkyan.popo.pouletmecaniquebackend.data.dto.GuildiEvent;

import java.util.List;

public interface IGuildiService {

    List<GuildiEvent> getGuildiEvents();

    List<String> getGuildiCategories();

}
