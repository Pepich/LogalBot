/*
 * Copyright (C) 2018 Logan Fick
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

import com.logaldeveloper.logalbot.Main;
import com.logaldeveloper.logalbot.audio.AudioPlayerSendHandler;
import com.logaldeveloper.logalbot.audio.TrackLoadHandler;
import com.logaldeveloper.logalbot.audio.TrackScheduler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.managers.AudioManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AudioUtil {
	private static final Logger logger = LoggerFactory.getLogger(AudioUtil.class);
	private static AudioPlayerManager playerManager;
	private static AudioPlayer player;
	private static AudioManager audioManager;

	public static void initialize(){
		playerManager = new DefaultAudioPlayerManager();
		AudioSourceManagers.registerRemoteSources(playerManager);
		player = playerManager.createPlayer();
		audioManager = Main.getGuild().getAudioManager();
		audioManager.setSelfDeafened(true);
		player.addListener(new TrackScheduler());
		logger.info("Audio environment ready!");
	}

	public static VoiceChannel getCurrentVoiceChannel(){
		return audioManager.getConnectedChannel();
	}

	static void closeAudioConnection(){
		audioManager.closeAudioConnection();
	}

	static void openAudioConnection(VoiceChannel channel){
		audioManager.setSendingHandler(new AudioPlayerSendHandler(player));
		audioManager.openAudioConnection(channel);
		audioManager.setSelfDeafened(true);
	}

	static boolean isAudioConnectionOpen(){
		return audioManager.isConnected();
	}

	public static boolean isTrackLoaded(){
		return !(player.getPlayingTrack() == null);
	}

	public static AudioTrack getLoadedTrack(){
		return player.getPlayingTrack();
	}

	public static boolean isPlayerPaused(){
		return player.isPaused();
	}

	public static void setPausedState(boolean pausedState){
		if (pausedState){
			logger.info("The audio player was paused.");
		} else {
			logger.info("The audio player was resumed.");
		}

		player.setPaused(pausedState);
	}

	public static int getVolume(){
		return player.getVolume();
	}

	public static void setVolume(int volume){
		logger.info("The audio player's volume was set to " + volume + "%.");
		player.setVolume(volume);
	}

	public static void playTrack(AudioTrack track){
		player.playTrack(track);
	}

	public static void stopTrack(){
		player.stopTrack();
	}

	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	public static boolean isAllowedChannelForAudioCommands(TextChannel channel){
		return channel.getName().equals(Main.getTextChannelNameForAudioCommands());
	}

	public static void findTrack(String query, User requester, TextChannel channel){
		playerManager.loadItem(query, new TrackLoadHandler(requester, channel));
	}
}