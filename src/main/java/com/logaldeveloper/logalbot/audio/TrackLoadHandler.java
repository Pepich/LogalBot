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

import com.logaldeveloper.logalbot.commands.PermissionManager;
import com.logaldeveloper.logalbot.utils.AudioUtil;
import com.logaldeveloper.logalbot.utils.EmojiUtil;
import com.logaldeveloper.logalbot.utils.StringUtil;
import com.logaldeveloper.logalbot.utils.TimeUtil;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class TrackLoadHandler implements AudioLoadResultHandler {
	private static final Logger logger = LoggerFactory.getLogger(TrackLoadHandler.class);
	private final User requester;
	private final TextChannel channel;

	public TrackLoadHandler(User requester, TextChannel channel){
		this.requester = requester;
		this.channel = channel;
	}

	@Override
	public void trackLoaded(AudioTrack track){
		if (TrackScheduler.isQueueFull()){
			channel.sendMessage(":card_box: Sorry " + requester.getAsMention() + ", but the queue is full.").queue();
			return;
		}

		if (track.getInfo().isStream){
			channel.sendMessage(":no_entry_sign: Sorry " + requester.getAsMention() + ", but streams cannot be added to the queue.").queue();
			return;
		}

		if ((track.getInfo().length <= 60000 || track.getInfo().length >= 900000) && !PermissionManager.isWhitelisted(requester)){
			channel.sendMessage(":no_entry_sign: Sorry " + requester.getAsMention() + ", but you can only add tracks between 1 and 15 minutes in length.").queue();
			return;
		}

		if (TrackScheduler.isQueued(track)){
			channel.sendMessage(":no_entry_sign: Sorry " + requester.getAsMention() + ", but that track is already queued.").queue();
			return;
		}

		channel.sendMessage(":notes: " + requester.getAsMention() + " added **" + StringUtil.sanatize(track.getInfo().title) + "** to the queue. (" + TimeUtil.formatTime(track.getInfo().length) + ")").queue();
		TrackScheduler.addToQueue(track, requester);
	}

	@SuppressWarnings("ResultOfMethodCallIgnored")
	@Override
	public void playlistLoaded(AudioPlaylist playlist){
		if (TrackScheduler.isQueueFull()){
			channel.sendMessage(":card_box: Sorry " + requester.getAsMention() + ", but the queue is full.").queue();
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
			if (TrackScheduler.isQueued(track)){
				channel.sendMessage(":no_entry_sign: Sorry " + requester.getAsMention() + ", but that track is already queued.").queue();
				return;
			}

			if (track.getInfo().isStream){
				channel.sendMessage(":no_entry_sign: Sorry " + requester.getAsMention() + ", but streams cannot be added to the queue.").queue();
				return;
			}

			if (PermissionManager.isWhitelisted(requester)){
				channel.sendMessage(":notes: " + requester.getAsMention() + " added **" + StringUtil.sanatize(track.getInfo().title) + "** to the queue. (" + TimeUtil.formatTime(track.getInfo().length) + ")").queue();
				TrackScheduler.addToQueue(track, requester);
			} else {
				if (!(track.getInfo().length <= 60000) && !(track.getInfo().length >= 900000)){
					channel.sendMessage(":notes: " + requester.getAsMention() + " added **" + StringUtil.sanatize(track.getInfo().title) + "** to the queue. (" + TimeUtil.formatTime(track.getInfo().length) + ")").queue();
					TrackScheduler.addToQueue(track, requester);
				} else {
					channel.sendMessage(":no_entry_sign: Sorry " + requester.getAsMention() + ", but you are not allowed to add tracks less than 1 minute or greater than 15 minutes in length.").queue();
				}
			}
		} else {
			if (PermissionManager.isWhitelisted(requester)){
				boolean shouldCountByZero = !AudioUtil.isTrackLoaded();
				ArrayList<AudioTrack> addedTracks = new ArrayList<>();
				long time = 0;
				for (AudioTrack playlistTrack : playlist.getTracks()){
					if (!TrackScheduler.isQueueFull()){
						if (!TrackScheduler.isQueued(playlistTrack) && !playlistTrack.getInfo().isStream){
							TrackScheduler.addToQueue(playlistTrack, requester);
							addedTracks.add(playlistTrack);
							time += playlistTrack.getInfo().length;
						}
					} else {
						break;
					}
				}

				StringBuilder reply = new StringBuilder(":notes: " + requester.getAsMention() + " added the following songs to the queue:\n");
				for (int i = 0; i < 11; i++){
					try{
						addedTracks.get(i); // Attempt to trigger an IndexOutOfBoundsException before we append to the string, otherwise we could get an incomplete track line added.
						if (shouldCountByZero){
							reply.append(EmojiUtil.intToEmoji(i)).append(" **").append(StringUtil.sanatize(addedTracks.get(i).getInfo().title)).append("** (").append(TimeUtil.formatTime(addedTracks.get(i).getDuration())).append(")\n");
						} else {
							reply.append(EmojiUtil.intToEmoji(i + 1)).append(" **").append(StringUtil.sanatize(addedTracks.get(i).getInfo().title)).append("** (").append(TimeUtil.formatTime(addedTracks.get(i).getDuration())).append(")\n");
						}
					} catch (IndexOutOfBoundsException exception){
						break;
					}
				}
				reply.append(":clock130: Total play time: ").append(TimeUtil.formatTime(time));
				channel.sendMessage(reply.toString()).queue();
			} else {
				channel.sendMessage(":no_entry_sign: Sorry " + requester.getAsMention() + ", but you are not allowed to add playlists to the queue.").queue();
			}
		}
	}

	@Override
	public void noMatches(){
		channel.sendMessage(":map: Sorry " + requester.getAsMention() + ", but I was not able to find that track.").queue();
	}

	@Override
	public void loadFailed(FriendlyException exception){
		channel.sendMessage(":sos: Sorry " + requester.getAsMention() + ", but an error occurred while trying to get that track!").queue();
		logger.error("An error occured while fetching a track!");
		exception.printStackTrace();
	}
}