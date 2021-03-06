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

import com.logaldeveloper.logalbot.commands.Command;
import com.logaldeveloper.logalbot.commands.CommandResponse;
import com.logaldeveloper.logalbot.commands.PermissionManager;
import com.logaldeveloper.logalbot.utils.AudioUtil;
import com.logaldeveloper.logalbot.utils.VoiceChannelUtil;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public final class Play implements Command {
	@Override
	public CommandResponse execute(String[] arguments, Member executor, TextChannel channel){
		Guild guild = channel.getGuild();
		if (AudioUtil.isTrackLoaded(guild) && !VoiceChannelUtil.isInCurrentVoiceChannel(executor)){
			return new CommandResponse("no_entry_sign", "Sorry " + executor.getAsMention() + ", but you need to be in voice channel `" + VoiceChannelUtil.getCurrentVoiceChannel(channel.getGuild()).getName() + "` in order to add songs to the queue.").setDeletionDelay(10, TimeUnit.SECONDS);
		}

		VoiceChannel targetChannel = VoiceChannelUtil.getCurrentVoiceChannelFromMember(executor);
		if (targetChannel == null){
			return new CommandResponse("no_entry_sign", "Sorry " + executor.getAsMention() + ", but you need to be in a voice channel in order to add songs to the queue.").setDeletionDelay(10, TimeUnit.SECONDS);
		}

		if (!guild.getSelfMember().hasPermission(targetChannel, Permission.VOICE_CONNECT) || !guild.getSelfMember().hasPermission(targetChannel, Permission.VOICE_SPEAK)){
			return new CommandResponse("no_entry_sign", "Sorry " + executor.getAsMention() + ", but I do not have the required permissions to use your current voice channel.").setDeletionDelay(10, TimeUnit.SECONDS);
		}

		if (arguments.length == 0){
			return new CommandResponse("no_entry_sign", "Sorry " + executor.getAsMention() + ", but you need to provide a search query or a link to a specific track or playlist.").setDeletionDelay(10, TimeUnit.SECONDS);
		}

		if (AudioUtil.getTrackScheduler(guild).isQueueLocked() && !PermissionManager.isWhitelisted(executor)){
			return new CommandResponse("lock", "Sorry " + executor.getAsMention() + ", but the queue is locked.").setDeletionDelay(10, TimeUnit.SECONDS);
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