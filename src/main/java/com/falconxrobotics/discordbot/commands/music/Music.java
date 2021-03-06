package com.falconxrobotics.discordbot.commands.music;

import java.util.HashMap;
import java.util.Map;

import com.github.raybipse.components.CommandGroup;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;


/**
 * @author RayBipse
 */
public class Music extends CommandGroup {
    private static Music instance;
    private Play play = new Play();
    protected Stop stop = new Stop();
    private Skip skip = new Skip();
    private Now now = new Now();
    private Queued queued = new Queued();
    private Loop loop = new Loop();
    private Pause pause = new Pause();
    private Forward forward = new Forward();

    public final AudioPlayerManager playerManager;
    protected final Map<Long, GuildMusicManager> musicManagers = new HashMap<Long, GuildMusicManager>();

    protected Music() {
        super("Music", "m");
        setDescription("Commands that is used to play music.");
        addChildren(play, stop, skip, now, queued, loop, pause, forward, new Help());

        playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioSourceManagers.registerLocalSource(playerManager);
    }

    /**
     * @return the sole instance of the class
     */
    public static Music getInstance() {
        if (instance == null) {
            instance = new Music();
        }
        return instance;
    }

    public GuildMusicManager getGuildMusicManager(Guild guild) {
        GuildMusicManager musicManager = musicManagers.get(guild.getIdLong());

        if (musicManager == null) {
            musicManager = new GuildMusicManager(playerManager, guild);
            musicManagers.put(guild.getIdLong(), musicManager);
        }

        guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());

        return musicManager;
    }

    public EmbedBuilder getEmbedTrackInfo(AudioTrackInfo info) {
        return new EmbedBuilder()
            .setTitle("Now Playing")
            .addField("Title", (info.uri.equals("https://www.youtube.com/watch?v=dQw4w9WgXcQ")) ? "rick" : info.title, false)
            .addField("Author", (info.uri.equals("https://www.youtube.com/watch?v=dQw4w9WgXcQ")) ? "epic dude" : info.author, false)
            .addField("Duration", inReadable(info.length), false)
            .addField("URI", info.uri, false)
            .addField("Is Stream", String.valueOf(info.isStream), false);
    }

    public String inReadable(long millis) {
        long seconds = millis/1000;
        long numberOfHours = (seconds % 86400 ) / 3600;
        long numberOfMinutes = ((seconds % 86400 ) % 3600 ) / 60; 
        long numberOfSeconds = ((seconds % 86400 ) % 3600 ) % 60;
        String result = "";
        if (numberOfHours != 0) {
            result+=numberOfHours+":";
        }
        if (numberOfMinutes != 0) {
            result+=(String.valueOf(numberOfMinutes).length() == 1 ? "0" : "") + numberOfMinutes+":";
        } else {
            result+="00:";
        }

        result+=(String.valueOf(numberOfSeconds).length() == 1 ? "0" : "") +  numberOfSeconds;
        return result;
    }
}