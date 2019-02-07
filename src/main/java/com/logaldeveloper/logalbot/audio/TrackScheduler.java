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
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public final class TrackScheduler extends AudioEventAdapter {
	private final Guild guild;
	private final ArrayList<AudioTrack> queue = new ArrayList<>(250);
	private final Logger logger = LoggerFactory.getLogger(TrackScheduler.class);
	private boolean queueLocked = false;
	private ScheduledFuture idleLogoutTask;

	public TrackScheduler(Guild guild){
		this.guild = guild;
	}

	@SuppressWarnings("ConstantConditions")
	public void addToQueue(AudioTrack track, Member requester){
		if (this.queueLocked && !PermissionManager.isWhitelisted(requester)){
			return;
		}

		if (this.isQueueFull()){
			return;
		}

		this.logger.info(requester.getEffectiveName() + " (" + requester.getUser().getId() + ") added '" + track.getInfo().title + "' to the queue in " + guild.getName() + " (" + guild.getId() + ").");
		this.queue.add(track);
		if (!AudioUtil.isTrackLoaded(this.guild)){
			// The inspector is suppressed here because the play command checks if the executor is in a voice channel before addToQueue is called.
			VoiceChannelUtil.joinVoiceChannel(VoiceChannelUtil.getCurrentVoiceChannelFromMember(requester));
			AudioUtil.playTrack(this.guild, this.queue.remove(0));
		}
	}

	public void removeFromQueue(int index){
		this.logger.info("Track '" + queue.remove(index).getInfo().title + "' has been removed from the queue in " + guild.getName() + " (" + guild.getId() + ").");
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
		return this.queue.size() >= 250;
	}

	public boolean isQueueEmpty(){
		return this.queue.isEmpty();
	}

	public boolean isQueueLocked(){
		return this.queueLocked;
	}

	public void setQueueLocked(boolean locked){
		this.queueLocked = locked;
	}

	public void clearQueue(){
		this.queue.clear();
	}

	public void shuffleQueue(){
		Collections.shuffle(this.queue);
	}

	public ArrayList<AudioTrack> getQueue(){
		return this.queue;
	}

	public void skipCurrentTrack(){
		if (AudioUtil.isTrackLoaded(this.guild)){
			this.logger.info("Track '" + AudioUtil.getLoadedTrack(this.guild).getInfo().title + "' in " + this.guild.getName() + " (" + this.guild.getId() + ") been skipped.");
			AudioUtil.stopTrack(this.guild);
		}
	}

	@Override
	public void onTrackStart(AudioPlayer player, AudioTrack track){
		this.logger.info("Track '" + track.getInfo().title + "' in " + this.guild.getName() + " (" + this.guild.getId() + ") has started.");
		if (this.idleLogoutTask != null && !this.idleLogoutTask.isDone()){
			this.logger.info("A track has started. Cancelling scheduled disconnect.");
			this.idleLogoutTask.cancel(true);
		}
		SkipManager.resetVotes(this.guild);
	}

	@Override
	public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason){
		this.logger.info("Track '" + track.getInfo().title + "' in " + this.guild.getName() + " (" + this.guild.getId() + ") has stopped.");
		if ((endReason.mayStartNext || endReason == AudioTrackEndReason.STOPPED) && this.queue.size() >= 1){
			AudioUtil.playTrack(this.guild, this.queue.remove(0));
		} else {
			AudioUtil.setVolume(this.guild, 10);
			AudioUtil.getTrackScheduler(this.guild).setQueueLocked(false);
			AudioUtil.setPausedState(this.guild, false);
			this.logger.info("Disconnecting from " + VoiceChannelUtil.getCurrentVoiceChannel(this.guild).getName() + " in " + this.guild.getName() + " (" + this.guild.getId() + ") in 1 minute...");
			this.idleLogoutTask = Scheduler.schedule(new IdleLogoutTask(this.guild), 1, TimeUnit.MINUTES);
		}
		SkipManager.resetVotes(this.guild);
	}
}