package fr.arkyan.popo.pouletmecaniquebackend.data.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;

@Data
@Entity
@NoArgsConstructor
@RequiredArgsConstructor
public class Event {

    @Id
    @NonNull
    private String url;

    @Column
    @NonNull
    private LocalDate date;

    @Column
    private boolean reminder24hDone;

    @Column
    private boolean reminder3hDone;

}
