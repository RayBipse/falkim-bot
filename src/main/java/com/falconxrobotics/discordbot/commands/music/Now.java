package com.falconxrobotics.discordbot.commands.music;

import java.awt.Color;

import com.github.raybipse.components.Command;
import com.github.raybipse.core.BotConfiguration;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * @author RayBipse
 */
public class Now extends Command {

    protected Now() {
        super("Now", "now");
        setDescription("Gives information about the currently playing track.");
        addExamples("");
        setSyntax("");
        setParent(Music::getInstance);
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String messageContent = event.getMessage().getContentDisplay();
        if (event.getAuthor().isBot())
            return;
        if (!getInputValidity(messageContent))
            return;

        GuildMusicManager guildMusicManager = Music.getInstance().getGuildMusicManager(event.getGuild());
        AudioTrack track = guildMusicManager.player.getPlayingTrack();
        EmbedBuilder builder = new EmbedBuilder();
        if (track == null) {
            builder.setTitle("No Tracks Playing").setColor(Color.ORANGE);
        } else {
            builder = Music.getInstance().getEmbedTrackInfo(track.getInfo())
                .setColor(BotConfiguration.getPromptColor())
                .addField("Position", Music.getInstance().inReadable(guildMusicManager.player.getPlayingTrack().getPosition()), false);
            
            String appen = "";
            if (guildMusicManager.scheduler.getLooped()) {
                appen = "Current track on loop. ";
            }
            if (guildMusicManager.scheduler.queue.size() > 0) {
                builder.setFooter(appen + guildMusicManager.scheduler.queue.size() + " more queued audio tracks.");
            } else if (guildMusicManager.scheduler.queue.size() == 0) {
                builder.setFooter(appen + "No more queued audio tracks.");
            }
        }

        event.getChannel().sendMessage(builder.build()).queue();
    }
}