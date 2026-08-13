package fr.arkyan.popo.pouletmecaniquebackend.service;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.List;

public interface IDiscordApiService {

    /**
     * Retrieves a list of text channels in the Discord server.
     * @return a list of text channels
     */
    List<TextChannel> getTextChannels();

    /**
     * Sends an error message to a specific text channel.
     * @param error message
     */
    void sendLogErrorMessage(String message);

    /**
     * Sends a message to a specific text channel.
     * @param message the message to send
     * @param channelId the ID of the channel to send the message to
     */
    void sendMessage(MessageEmbed message, String channelId);

    /**
     * Retrieves a list of mentionable roles in the Discord server.
     * @return a list of mentionable roles
     */
    List<Role> getMentionnableRoles();

}
