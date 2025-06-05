package fr.arkyan.popo.pouletmecaniquebackend.data.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class Feed {

    @Id
    @NonNull
    private String url;

    private LocalDate date = LocalDate.now();

}
