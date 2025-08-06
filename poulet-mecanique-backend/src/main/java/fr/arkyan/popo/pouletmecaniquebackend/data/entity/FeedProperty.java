package fr.arkyan.popo.pouletmecaniquebackend.data.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class FeedProperty {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private String label;

    @Column
    private String discordChannel;

    @Column
    private String url;

    @Column
    private String icon;

    @Column
    private String filter;

}
