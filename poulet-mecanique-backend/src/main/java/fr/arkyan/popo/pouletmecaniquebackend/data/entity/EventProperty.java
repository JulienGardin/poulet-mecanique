package fr.arkyan.popo.pouletmecaniquebackend.data.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class EventProperty {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private String label;

    @Column
    private String category;

    @Column
    private String discordChannel;

}
