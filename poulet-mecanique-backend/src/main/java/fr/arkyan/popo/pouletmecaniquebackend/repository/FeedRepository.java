package fr.arkyan.popo.pouletmecaniquebackend.repository;

import fr.arkyan.popo.pouletmecaniquebackend.data.entity.Feed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.Date;

public interface FeedRepository extends JpaRepository<Feed, String> {

    @Query("DELETE FROM Feed f WHERE f.date < :time")
    void removeOlderThan(LocalDate time);
}
