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

import com.logaldeveloper.logalbot.commands.CommandResponse;
import com.logaldeveloper.logalbot.commands.PermissionManager;
import com.logaldeveloper.logalbot.utils.AudioUtil;
import com.logaldeveloper.logalbot.utils.TrackUtil;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public final class TrackLoadHandler implements AudioLoadResultHandler {
	private final Logger logger = LoggerFactory.getLogger(TrackLoadHandler.class);
	private final User requester;
	private final TextChannel channel;

	public TrackLoadHandler(User requester, TextChannel channel){
		this.requester = requester;
		this.channel = channel;
	}

	@Override
	public void trackLoaded(AudioTrack track){
		CommandResponse response;
		Guild guild = channel.getGuild();
		TrackScheduler scheduler = AudioUtil.getTrackScheduler(guild);

		if (scheduler.isQueueFull()){
			response = new CommandResponse("card_box", "Sorry " + requester.getAsMention() + ", but the queue is full.").setDeletionDelay(10, TimeUnit.SECONDS);
			response.sendResponse(channel);
			return;
		}

		if (track.getInfo().isStream){
			response = new CommandResponse("no_entry_sign", "Sorry " + requester.getAsMention() + ", but streams cannot be added to the queue.").setDeletionDelay(10, TimeUnit.SECONDS);
			response.sendResponse(channel);
			return;
		}

		if ((track.getInfo().length <= 60000 || track.getInfo().length >= 900000) && !PermissionManager.isWhitelisted(requester, guild)){
			response = new CommandResponse("no_entry_sign", "Sorry " + requester.getAsMention() + ", but you can only add tracks between 1 and 15 minutes in length.").setDeletionDelay(10, TimeUnit.SECONDS);
			response.sendResponse(channel);
			return;
		}

		if (scheduler.isQueued(track)){
			response = new CommandResponse("no_entry_sign", "Sorry " + requester.getAsMention() + ", but that track is already queued.").setDeletionDelay(10, TimeUnit.SECONDS);
			response.sendResponse(channel);
			return;
		}

		scheduler.addToQueue(track, requester);
		response = new CommandResponse("notes", requester.getAsMention() + " added the following track to the queue:");
		response.attachEmbed(TrackUtil.generateTrackInfoEmbed(track));
		response.sendResponse(channel);
	}

	@Override
	public void playlistLoaded(AudioPlaylist playlist){
		CommandResponse response;
		Guild guild = channel.getGuild();
		TrackScheduler scheduler = AudioUtil.getTrackScheduler(guild);

		if (scheduler.isQueueFull()){
			response = new CommandResponse("card_box", "Sorry " + requester.getAsMention() + ", but the queue is full.").setDeletionDelay(10, TimeUnit.SECONDS);
			response.sendResponse(channel);
			return;
		}

		AudioTrack selectedTrack = playlist.getSelectedTrack();
		AudioTrack track = null;
		if (!playlist.isSearchResult() && selectedTrack != null){
			track = selectedTrack;
		} else if (playlist.isSearchResult()){
			track = playlist.getTracks().get(0);
		}

		if (track != null){
			if (scheduler.isQueued(track)){
				response = new CommandResponse("no_entry_sign", "Sorry " + requester.getAsMention() + ", but that track is already queued.").setDeletionDelay(10, TimeUnit.SECONDS);
				response.sendResponse(channel);
				return;
			}

			if (track.getInfo().isStream){
				response = new CommandResponse("no_entry_sign", "Sorry " + requester.getAsMention() + ", but streams cannot be added to the queue.").setDeletionDelay(10, TimeUnit.SECONDS);
				response.sendResponse(channel);
				return;
			}

			if (PermissionManager.isWhitelisted(requester, guild)){
				scheduler.addToQueue(track, requester);
				response = new CommandResponse("notes", requester.getAsMention() + " added the following track to the queue:");
				response.attachEmbed(TrackUtil.generateTrackInfoEmbed(track));
				response.sendResponse(channel);
			} else {
				if (!(track.getInfo().length <= 60000) && !(track.getInfo().length >= 900000)){
					scheduler.addToQueue(track, requester);
					response = new CommandResponse("notes", requester.getAsMention() + " added the following track to the queue:");
					response.attachEmbed(TrackUtil.generateTrackInfoEmbed(track));
					response.sendResponse(channel);
				} else {
					response = new CommandResponse("no_entry_sign", "Sorry " + requester.getAsMention() + ", but you are not allowed to add tracks less than 1 minute or greater than 15 minutes in length.").setDeletionDelay(10, TimeUnit.SECONDS);
					response.sendResponse(channel);
				}
			}
		} else {
			if (PermissionManager.isWhitelisted(requester, guild)){
				ArrayList<AudioTrack> addedTracks = new ArrayList<>();
				for (AudioTrack playlistTrack : playlist.getTracks()){
					if (!scheduler.isQueueFull()){
						if (!scheduler.isQueued(playlistTrack) && !playlistTrack.getInfo().isStream){
							scheduler.addToQueue(playlistTrack, requester);
							addedTracks.add(playlistTrack);
						}
					} else {
						break;
					}
				}

				if (addedTracks.size() == 0){
					response = new CommandResponse("no_entry_sign", "Sorry " + requester.getAsMention() + ", but none of the tracks in that playlist could be added.").setDeletionDelay(10, TimeUnit.SECONDS);
					response.sendResponse(channel);
				}

				response = new CommandResponse("notes", requester.getAsMention() + " added the following tracks to the queue:");
				response.attachEmbed(TrackUtil.generateTrackListInfoEmbed(addedTracks));
				response.sendResponse(channel);
			} else {
				response = new CommandResponse("no_entry_sign", "Sorry " + requester.getAsMention() + ", but you are not allowed to add playlists to the queue.").setDeletionDelay(10, TimeUnit.SECONDS);
				response.sendResponse(channel);
			}
		}
	}

	@Override
	public void noMatches(){
		CommandResponse response = new CommandResponse("map", "Sorry " + requester.getAsMention() + ", but I was not able to find that track.").setDeletionDelay(10, TimeUnit.SECONDS);
		response.sendResponse(channel);
	}

	@Override
	public void loadFailed(FriendlyException exception){
		CommandResponse response;
		if (exception.getMessage().equals("Unknown file format.")){
			response = new CommandResponse("question", "Sorry " + requester.getAsMention() + ", but I do not recognize the format of that track.").setDeletionDelay(10, TimeUnit.SECONDS);
		} else {
			logger.error("An error occurred for " + channel.getGuild().getName() + " (" + channel.getGuild().getId() + ") while trying to load a track!", exception);
			response = new CommandResponse("sos", "Sorry " + requester.getAsMention() + ", but an error occurred while trying to get that track!").setDeletionDelay(10, TimeUnit.SECONDS);
		}
		response.sendResponse(channel);
	}
}