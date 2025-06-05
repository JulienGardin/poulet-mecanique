package fr.arkyan.popo.pouletmecaniquebackend.repository;

import fr.arkyan.popo.pouletmecaniquebackend.data.entity.FeedProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedPropertyRepository extends JpaRepository<FeedProperty, Long> {

}
