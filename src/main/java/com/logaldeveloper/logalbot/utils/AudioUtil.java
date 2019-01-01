/*
 * Copyright (C) 2019 Logan Fick
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.logaldeveloper.logalbot.utils;

import com.logaldeveloper.logalbot.audio.AudioPlayerSendHandler;
import com.logaldeveloper.logalbot.audio.TrackLoadHandler;
import com.logaldeveloper.logalbot.audio.TrackScheduler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.managers.AudioManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public final class AudioUtil {
	private static final Logger logger = LoggerFactory.getLogger(AudioUtil.class);

	private static final AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
	private static final HashMap<String, AudioPlayer> players = new HashMap<>();
	private static final HashMap<String, TrackScheduler> schedulers = new HashMap<>();

	public static void initializePlayerManager(){
		AudioSourceManagers.registerRemoteSources(playerManager);
	}

	public static void initialize(Guild guild){
		players.put(guild.getId(), playerManager.createPlayer());
		guild.getAudioManager().setSelfDeafened(true);
		schedulers.put(guild.getId(), new TrackScheduler(guild));
		players.get(guild.getId()).addListener(schedulers.get(guild.getId()));

		setVolume(guild, 10);
		getTrackScheduler(guild).setQueueLocked(false);
		setPausedState(guild, false);

		logger.info("Audio environment initialized for guild ID " + guild.getId() + ".");
	}

	public static boolean isInitialized(Guild guild){
		return ((players.get(guild.getId()) != null) && (schedulers.get(guild.getId()) != null));
	}

	public static VoiceChannel getCurrentVoiceChannel(Guild guild){
		return guild.getAudioManager().getConnectedChannel();
	}

	public static void closeAudioConnection(Guild guild){
		guild.getAudioManager().closeAudioConnection();
	}

	public static void openAudioConnection(VoiceChannel channel){
		Guild guild = channel.getGuild();
		AudioManager audioManager = guild.getAudioManager();

		audioManager.setSendingHandler(new AudioPlayerSendHandler(players.get(guild.getId())));
		audioManager.openAudioConnection(channel);
		audioManager.setSelfDeafened(true);
	}

	public static boolean isAudioConnectionOpen(Guild guild){
		return guild.getAudioManager().isConnected();
	}

	public static boolean isTrackLoaded(Guild guild){
		return !(getLoadedTrack(guild) == null);
	}

	public static void playTrack(Guild guild, AudioTrack track){
		players.get(guild.getId()).playTrack(track);
	}

	public static void stopTrack(Guild guild){
		players.get(guild.getId()).stopTrack();
	}

	public static AudioTrack getLoadedTrack(Guild guild){
		return players.get(guild.getId()).getPlayingTrack();
	}

	public static boolean isPlayerPaused(Guild guild){
		return players.get(guild.getId()).isPaused();
	}

	public static void setPausedState(Guild guild, boolean pausedState){
		if (pausedState){
			logger.info("The audio player was paused.");
		} else {
			logger.info("The audio player was resumed.");
		}

		players.get(guild.getId()).setPaused(pausedState);
	}

	public static int getVolume(Guild guild){
		return players.get(guild.getId()).getVolume();
	}

	public static void setVolume(Guild guild, int volume){
		logger.info("The audio player's volume was set to " + volume + "%.");
		players.get(guild.getId()).setVolume(volume);
	}

	public static void findTrack(String query, User requester, TextChannel channel){
		playerManager.loadItem(query, new TrackLoadHandler(requester, channel));
	}

	public static TrackScheduler getTrackScheduler(Guild guild){
		return schedulers.get(guild.getId());
	}
}