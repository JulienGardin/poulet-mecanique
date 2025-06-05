package fr.arkyan.popo.pouletmecaniquebackend.data.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDate;

@Entity
@Data
@RequiredArgsConstructor
public class Feed {

    @Id
    @NonNull
    private String url;

    private LocalDate date = LocalDate.now();

}
