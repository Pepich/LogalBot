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

package com.logaldeveloper.logalbot.commands.audio;

import com.logaldeveloper.logalbot.Main;
import com.logaldeveloper.logalbot.commands.Command;
import com.logaldeveloper.logalbot.commands.CommandResponse;
import com.logaldeveloper.logalbot.commands.PermissionManager;
import com.logaldeveloper.logalbot.utils.AudioUtil;
import com.logaldeveloper.logalbot.utils.VoiceChannelUtil;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class Play implements Command {
	@Override
	public void initialize(){
	}

	@Override
	public CommandResponse execute(String[] arguments, User executor, TextChannel channel){
		if (!AudioUtil.isAllowedChannelForAudioCommands(channel)){
			return new CommandResponse("no_entry_sign", "Sorry " + executor.getAsMention() + ", but audio commands can only be used in text channels named `" + Main.getTextChannelNameForAudioCommands() + "`.").setDeletionDelay(10, TimeUnit.SECONDS);
		}

		if (AudioUtil.getTrackScheduler(channel.getGuild()).isQueueLocked() && !PermissionManager.isWhitelisted(executor, channel.getGuild())){
			return new CommandResponse("lock", "Sorry " + executor.getAsMention() + ", but the queue is locked.").setDeletionDelay(10, TimeUnit.SECONDS);
		}

		if (AudioUtil.isTrackLoaded(channel.getGuild()) && !VoiceChannelUtil.isInCurrentVoiceChannel(channel.getGuild(), executor)){
			return new CommandResponse("no_entry_sign", "Sorry " + executor.getAsMention() + ", but you need to be in voice channel `" + VoiceChannelUtil.getCurrentVoiceChannel(channel.getGuild()).getName() + "` in order to add songs to the queue.").setDeletionDelay(10, TimeUnit.SECONDS);
		}

		if (VoiceChannelUtil.getCurrentVoiceChannelFromUser(channel.getGuild(), executor) == null){
			return new CommandResponse("no_entry_sign", "Sorry " + executor.getAsMention() + ", but you need to be in a voice channel in order to add songs to the queue.").setDeletionDelay(10, TimeUnit.SECONDS);
		}

		if (arguments.length == 0){
			return new CommandResponse("no_entry_sign", "Sorry " + executor.getAsMention() + ", but you need to provide a search query or a link to a specific track or playlist.").setDeletionDelay(10, TimeUnit.SECONDS);
		}

		boolean isLink;
		try{
			new URL(arguments[0]);
			isLink = true;
		} catch (MalformedURLException exception){
			isLink = false;
		}

		StringBuilder query;
		if (isLink){
			query = new StringBuilder(arguments[0]);
		} else {
			query = new StringBuilder("ytsearch:");
			for (String part : arguments){
				query.append(part).append(" ");
			}
		}

		AudioUtil.findTrack(query.toString(), executor, channel);
		return null;
	}
}