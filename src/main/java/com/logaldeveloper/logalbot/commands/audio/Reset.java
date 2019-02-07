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
import com.logaldeveloper.logalbot.utils.AudioUtil;
import com.logaldeveloper.logalbot.utils.VoiceChannelUtil;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.concurrent.TimeUnit;

public final class Reset implements Command {
	@Override
	public CommandResponse execute(String[] arguments, Member executor, TextChannel channel){
		Guild guild = channel.getGuild();
		if (AudioUtil.isTrackLoaded(guild) && !VoiceChannelUtil.isInCurrentVoiceChannel(executor)){
			return new CommandResponse("no_entry_sign", "Sorry " + executor.getAsMention() + ", but you need to be in voice channel `" + VoiceChannelUtil.getCurrentVoiceChannel(guild).getName() + "` in order to reset the audio player.").setDeletionDelay(10, TimeUnit.SECONDS);
		}

		AudioUtil.getTrackScheduler(guild).clearQueue();
		if (AudioUtil.isTrackLoaded(guild)){
			AudioUtil.stopTrack(guild);
		}

		return new CommandResponse("recycle", executor.getAsMention() + " has stopped the current track and reset the queue.");
	}
}