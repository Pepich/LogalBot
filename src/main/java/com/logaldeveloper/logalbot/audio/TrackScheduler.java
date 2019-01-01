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

package com.logaldeveloper.logalbot.audio;

import com.logaldeveloper.logalbot.commands.PermissionManager;
import com.logaldeveloper.logalbot.tasks.IdleLogoutTask;
import com.logaldeveloper.logalbot.utils.AudioUtil;
import com.logaldeveloper.logalbot.utils.Scheduler;
import com.logaldeveloper.logalbot.utils.SkipManager;
import com.logaldeveloper.logalbot.utils.VoiceChannelUtil;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public final class TrackScheduler extends AudioEventAdapter {
	private final Guild guild;
	private final ArrayList<AudioTrack> queue = new ArrayList<>();
	private final Logger logger = LoggerFactory.getLogger(TrackScheduler.class);
	private boolean queueLocked = false;
	private ScheduledFuture idleLogoutTask;

	public TrackScheduler(Guild guild){
		this.guild = guild;
	}

	@SuppressWarnings("ConstantConditions")
	public void addToQueue(AudioTrack track, User requester){
		if (queueLocked && !PermissionManager.isWhitelisted(requester, guild)){
			return;
		}

		if (isQueueFull()){
			return;
		}

		this.logger.info("'" + requester.getName() + "' added '" + track.getInfo().title + "' to the queue.");
		this.queue.add(track);
		if (!AudioUtil.isTrackLoaded(guild)){
			// The inspector is suppressed here because the play command checks if the executor is in a voice channel before addToQueue is called.
			VoiceChannelUtil.joinVoiceChannel(VoiceChannelUtil.getCurrentVoiceChannelFromUser(guild, requester));
			AudioUtil.playTrack(guild, queue.get(0));
			queue.remove(0);
		}
	}

	public void removeFromQueue(int index){
		logger.info("Track '" + queue.get(index).getInfo().title + "' has been removed from the queue.");
		queue.remove(index);
	}

	public boolean isQueued(AudioTrack track){
		for (AudioTrack queuedTrack : queue){
			if (track.getInfo().identifier.equals(queuedTrack.getInfo().identifier)){
				return true;
			}
		}
		return false;
	}

	public boolean isQueueFull(){
		return queue.size() >= 10;
	}

	public boolean isQueueEmpty(){
		return queue.size() == 0;
	}

	public boolean isQueueLocked(){
		return queueLocked;
	}

	public void setQueueLocked(boolean locked){
		queueLocked = locked;
	}

	public void clearQueue(){
		queue.clear();
	}

	public ArrayList<AudioTrack> getQueue(){
		return queue;
	}

	public void skipCurrentTrack(){
		if (AudioUtil.isTrackLoaded(guild)){
			logger.info("Track '" + AudioUtil.getLoadedTrack(guild).getInfo().title + "' has been skipped.");
			AudioUtil.stopTrack(guild);
		}
	}

	@Override
	public void onTrackStart(AudioPlayer player, AudioTrack track){
		logger.info("Track '" + track.getInfo().title + "' has started.");
		if (idleLogoutTask != null && !idleLogoutTask.isDone()){
			logger.info("A track has started. Cancelling scheduled disconnect.");
			idleLogoutTask.cancel(true);
		}
		SkipManager.resetVotes(guild);
	}

	@Override
	public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason){
		System.gc();
		logger.info("Track '" + track.getInfo().title + "' has stopped.");
		if ((endReason.mayStartNext || endReason == AudioTrackEndReason.STOPPED) && queue.size() >= 1){
			AudioUtil.playTrack(guild, queue.get(0));
			queue.remove(0);
		} else {
			AudioUtil.setVolume(guild, 10);
			AudioUtil.getTrackScheduler(guild).setQueueLocked(false);
			AudioUtil.setPausedState(guild, false);
			logger.info("Disconnecting from voice channel '" + VoiceChannelUtil.getCurrentVoiceChannel(guild).getName() + "' in 1 minute...");
			idleLogoutTask = Scheduler.schedule(new IdleLogoutTask(guild), 1, TimeUnit.MINUTES);
		}
		SkipManager.resetVotes(guild);
	}
}