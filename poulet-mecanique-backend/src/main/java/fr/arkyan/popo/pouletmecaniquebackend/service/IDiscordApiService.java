package fr.arkyan.popo.pouletmecaniquebackend.service;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.List;

public interface IDiscordApiService {

    List<Category> getCategories();

     * Retrieves a list of text channels in the Discord server.
     * @return a list of text channels
    List<TextChannel> getTextChannels();

    void sendLogErrorMessage(String message);

    void sendMessage(MessageEmbed message, String channelId);

}
