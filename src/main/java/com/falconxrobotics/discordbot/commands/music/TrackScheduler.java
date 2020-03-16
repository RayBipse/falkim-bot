package com.falconxrobotics.discordbot.commands.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.Color;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * This class schedules tracks for the audio player. It contains the queue of
 * tracks.
 * 
 * @author sedmelluq
 * @author RayBipse
 */
public class TrackScheduler extends AudioEventAdapter {
    private final AudioPlayer player;
    private final BlockingQueue<AudioTrack> queue;
    private final GuildMusicManager guildMusicManager;

    /**
     * @param player The audio player this scheduler uses
     */
    public TrackScheduler(AudioPlayer player, GuildMusicManager guildMusicManager) {
        this.player = player;
        this.guildMusicManager = guildMusicManager;
        this.queue = new LinkedBlockingQueue<>();
    }

    /**
     * Add the next track to queue or play right away if nothing is in the queue.
     *
     * @param track The track to play or add to queue.
     */
    public void queue(AudioTrack track) {
        // Calling startTrack with the noInterrupt set to true will start the track only
        // if nothing is currently playing. If
        // something is playing, it returns false and does nothing. In that case the
        // player was already playing so this
        // track goes to the queue instead.
        if (!player.startTrack(track, true)) {
            queue.offer(track);
        }
    }

    /**
     * Start the next track, stopping the current one if it is playing.
     */
    public void nextTrack() {
        // Start the next track, regardless of if something is already playing or not.
        // In case queue was empty, we are
        // giving null to startTrack, which is a valid argument and will simply stop the
        // player.
        AudioTrack track = queue.poll();
        player.startTrack(track, false);
        EmbedBuilder builder = new EmbedBuilder()
            .setTitle("Now Playing")
            .addField("Title", track.getInfo().title, false)
            .addField("Author", track.getInfo().author, false)
            .addField("Duration", Music.getInstance().inReadable(track.getInfo().length), false)
            .addField("URI", track.getInfo().uri, false)
            .setColor(Color.ORANGE);
        guildMusicManager.channel.sendMessage(builder.build()).queue();
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        // Only start the next track if the end reason is suitable for it (FINISHED or
        // LOAD_FAILED)
        if (endReason.mayStartNext) {
            nextTrack();
        } else {
            Music.getInstance().stop.invoke(guildMusicManager.guild);
        }
    }
}