package fr.arkyan.popo.pouletmecaniquebackend.repository;

import fr.arkyan.popo.pouletmecaniquebackend.data.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface EventRepository extends JpaRepository<Event, String> {

    @Modifying
    @Query("DELETE FROM Event e WHERE e.date < :time")
    void removeOlderThan(LocalDate localDate);
}
