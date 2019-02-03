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

package com.logaldeveloper.logalbot.commands.audio;

import com.logaldeveloper.logalbot.audio.TrackScheduler;
import com.logaldeveloper.logalbot.commands.Command;
import com.logaldeveloper.logalbot.commands.CommandManager;
import com.logaldeveloper.logalbot.commands.CommandResponse;
import com.logaldeveloper.logalbot.utils.*;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

import java.util.concurrent.TimeUnit;

public final class Remove implements Command {
	@Override
	public CommandResponse execute(String[] arguments, User executor, TextChannel channel){
		TrackScheduler scheduler = AudioUtil.getTrackScheduler(channel.getGuild());
		if (scheduler.isQueueEmpty()){
			return new CommandResponse("no_entry_sign", "Sorry " + executor.getAsMention() + ", but there are no tracks in the queue.").setDeletionDelay(10, TimeUnit.SECONDS);
		}

		Guild guild = channel.getGuild();
		if (AudioUtil.isTrackLoaded(guild) && !VoiceChannelUtil.isInCurrentVoiceChannel(guild, executor)){
			return new CommandResponse("no_entry_sign", "Sorry " + executor.getAsMention() + ", but you need to be in voice channel `" + VoiceChannelUtil.getCurrentVoiceChannel(guild).getName() + "` in order to remove tracks from the queue.").setDeletionDelay(10, TimeUnit.SECONDS);
		}

		if (arguments.length == 0){
			CommandResponse response = new CommandResponse("question", executor.getAsMention() + ", which track would you like to remove from the top of the queue?");
			response.attachEmbed(TrackUtil.generateTrackListInfoEmbed(scheduler.getQueue(), true));

			for (int i = 0; i < scheduler.getQueue().size(); i++){
				final int trackNumber = i + 1;
				if (trackNumber == 11){
					break;
				}

				response.addReactionCallback(StringUtil.intToUnicodeEmoji(trackNumber), (reactor, messageID) -> {
					ReactionCallbackManager.unregisterMessage(messageID);
					CommandManager.executeCommand(("remove " + trackNumber).split(" "), reactor, channel);
				});
			}

			response.setReactionCallbackTarget(executor);
			response.setDeletionDelay(30, TimeUnit.SECONDS);
			return response;
		}

		int index;
		try{
			index = Integer.parseInt(arguments[0]);
		} catch (NumberFormatException exception){
			return new CommandResponse("no_entry_sign", "Sorry " + executor.getAsMention() + ", but the index must be an integer.").setDeletionDelay(10, TimeUnit.SECONDS);
		}

		try{
			AudioTrack removedTrack = scheduler.getQueue().get(index - 1);

			scheduler.removeFromQueue(index - 1);
			CommandResponse response = new CommandResponse("scissors", executor.getAsMention() + " removed the following track from the queue:");
			response.attachEmbed(TrackUtil.generateTrackInfoEmbed(removedTrack));
			return response;
		} catch (IndexOutOfBoundsException exception){
			return new CommandResponse("no_entry_sign", "Sorry " + executor.getAsMention() + ", but that index is outside the bounds of the queue.").setDeletionDelay(10, TimeUnit.SECONDS);
		}
	}
}