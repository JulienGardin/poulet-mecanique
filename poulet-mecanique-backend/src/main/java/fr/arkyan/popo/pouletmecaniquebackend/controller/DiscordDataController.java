package fr.arkyan.popo.pouletmecaniquebackend.controller;

import fr.arkyan.popo.pouletmecaniquebackend.data.dto.DiscordChannel;
import fr.arkyan.popo.pouletmecaniquebackend.service.IDiscordApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/discord")
public class DiscordDataController {

    @Autowired
    private IDiscordApiService discordApiService;

    @GetMapping("/channels")
    public List<DiscordChannel> getDiscordChannels() {
        return discordApiService.getTextChannels().stream().map(
                textChannel -> new DiscordChannel(
                        textChannel.getId(),
                        textChannel.getName(),
                        textChannel.getParentCategory() != null ? textChannel.getParentCategory().getName() : ""
                )
        ).toList();
    }

}
