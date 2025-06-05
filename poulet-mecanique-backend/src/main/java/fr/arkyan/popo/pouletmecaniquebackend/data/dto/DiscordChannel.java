package fr.arkyan.popo.pouletmecaniquebackend.data.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DiscordChannel {

    private String id;
    private String name;
    private String category;

    @JsonProperty("label")
    public String getLabel() {
        return category + " - " + name;
    }

}
