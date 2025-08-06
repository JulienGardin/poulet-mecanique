package fr.arkyan.popo.pouletmecaniquebackend.service.impl;

import fr.arkyan.popo.pouletmecaniquebackend.service.IDiscordApiService;
import jakarta.annotation.PostConstruct;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.EnumSet;
import java.util.List;

@Service
public class DiscordApiService implements IDiscordApiService {

    @Value("${discord.token}")
    private String discordToken;

    @Value("${discord.guildId}")
    private String discordGuildId;

    @Value("${discord.logChannelId}")
    private String discordLogChannelId;

    private JDA jda;
    private Guild guild;
    private TextChannel logChannel;

    @PostConstruct
    private void initDiscordConnection() throws InterruptedException {
        jda = JDABuilder.createLight(discordToken, EnumSet.of(GatewayIntent.DIRECT_MESSAGES)).setAutoReconnect(true).build();
        jda.awaitReady(); // Ensure JDA is ready before proceeding
        guild = jda.getGuildById(discordGuildId);
        assert guild != null : "Guild with ID " + discordGuildId + " not found. Please check your configuration.";
        logChannel = guild.getTextChannelById(discordLogChannelId);
        assert logChannel != null : "Log channel not found in the specified guild. Please check the configuration.";
    }

    @Override
    public List<TextChannel> getTextChannels() {
        return guild.getTextChannels();
    }

    @Override
    public void sendLogErrorMessage(String message) {
        MessageEmbed msg = new EmbedBuilder()
                .setTitle("Poulet Mécanique en détresse !")
                .setDescription(message)
                .setColor(Color.red)
                .build();
        logChannel.sendMessageEmbeds(msg).complete();
    }

    @Override
    public void sendMessage(MessageEmbed message, String channelId) {
        TextChannel channel = jda.getTextChannelById(channelId);
        if (channel != null) {
            channel.sendMessageEmbeds(message).queue();
        } else {
            throw new IllegalArgumentException("Channel with ID " + channelId + " not found.");
        }
    }


}
