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

package com.logaldeveloper.logalbot.audio;

import com.logaldeveloper.logalbot.Main;
import com.logaldeveloper.logalbot.commands.CommandManager;
import com.logaldeveloper.logalbot.commands.PermissionManager;
import com.logaldeveloper.logalbot.threads.IdleLogoutThread;
import com.logaldeveloper.logalbot.utils.AudioUtil;
import com.logaldeveloper.logalbot.utils.Scheduler;
import com.logaldeveloper.logalbot.utils.VoiceChannelUtil;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class TrackScheduler extends AudioEventAdapter {
	private static final ArrayList<AudioTrack> queue = new ArrayList<>();
	private static final Logger logger = LoggerFactory.getLogger(TrackScheduler.class);
	private static boolean queueLocked = false;
	private static ScheduledFuture idleLogoutTask;

	@SuppressWarnings("ConstantConditions")
	static void addToQueue(AudioTrack track, User requester){
		if (queueLocked && !PermissionManager.isWhitelisted(requester)){
			return;
		}

		if (TrackScheduler.isQueueFull()){
			return;
		}

		logger.info("'" + requester.getName() + "' added '" + track.getInfo().title + "' to the queue.");
		queue.add(track);
		if (!AudioUtil.isTrackLoaded()){
			// The inspector is suppressed here because the play command checks if the executor is in a voice channel before addToQueue is called.
			VoiceChannelUtil.joinVoiceChannel(VoiceChannelUtil.getCurrentVoiceChannelFromUser(requester));
			AudioUtil.playTrack(queue.get(0));
			queue.remove(0);
		}
	}

	static boolean isQueued(AudioTrack track){
		for (AudioTrack queuedTrack : queue){
			if (track.getInfo().identifier.equals(queuedTrack.getInfo().identifier)){
				return true;
			}
		}
		return false;
	}

	static boolean isQueueFull(){
		return queue.size() >= 10;
	}

	public static boolean isQueueEmpty(){
		return queue.size() == 0;
	}

	public static boolean isQueueLocked(){
		return queueLocked;
	}

	public static void setQueueLocked(boolean locked){
		queueLocked = locked;
	}

	public static void clearQueue(){
		queue.clear();
	}

	public static ArrayList<AudioTrack> getQueue(){
		return queue;
	}

	public static void removeFromQueue(int index){
		logger.info("Track '" + queue.get(index).getInfo().title + "' has been removed from the queue.");
		queue.remove(index);
	}

	public static void skipCurrentTrack(){
		if (AudioUtil.isTrackLoaded()){
			logger.info("Track '" + AudioUtil.getLoadedTrack().getInfo().title + "' has been skipped.");
			AudioUtil.stopTrack();
		}
	}

	@Override
	public void onTrackStart(AudioPlayer player, AudioTrack track){
		logger.info("Track '" + track.getInfo().title + "' has started.");
		Main.getJDA().getPresence().setGame(Game.listening(track.getInfo().title));
		CommandManager.reinitializeCommand("skip");
		if (idleLogoutTask != null && !idleLogoutTask.isDone()){
			logger.info("A track has started. Cancelling scheduled disconnect.");
			idleLogoutTask.cancel(true);
		}

	}

	@Override
	public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason){
		System.gc();
		logger.info("Track '" + track.getInfo().title + "' has stopped.");
		if ((endReason.mayStartNext || endReason == AudioTrackEndReason.STOPPED) && queue.size() >= 1){
			AudioUtil.playTrack(queue.get(0));
			queue.remove(0);
		} else {
			CommandManager.reinitializeCommand("volume");
			CommandManager.reinitializeCommand("lock");
			CommandManager.reinitializeCommand("pause");
			Main.getJDA().getPresence().setGame(Game.listening("Silence"));
			logger.info("Disconnecting from voice channel '" + VoiceChannelUtil.getCurrentVoiceChannel().getName() + "' in 1 minute...");
			idleLogoutTask = Scheduler.schedule(new IdleLogoutThread(), 1, TimeUnit.MINUTES);
		}
		CommandManager.reinitializeCommand("skip");
	}
}