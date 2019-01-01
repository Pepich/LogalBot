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

import com.logaldeveloper.logalbot.Main;
import com.logaldeveloper.logalbot.audio.TrackScheduler;
import com.logaldeveloper.logalbot.commands.Command;
import com.logaldeveloper.logalbot.commands.CommandResponse;
import com.logaldeveloper.logalbot.utils.AudioUtil;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

import java.util.concurrent.TimeUnit;

public final class Lock implements Command {
	@Override
	public CommandResponse execute(String[] arguments, User executor, TextChannel channel){
		if (!AudioUtil.isAllowedChannelForAudioCommands(channel)){
			return new CommandResponse("no_entry_sign", "Sorry " + executor.getAsMention() + ", but audio commands can only be used in text channels named `" + Main.getTextChannelNameForAudioCommands() + "`.").setDeletionDelay(10, TimeUnit.SECONDS);
		}

		TrackScheduler scheduler = AudioUtil.getTrackScheduler(channel.getGuild());
		if (scheduler.isQueueLocked()){
			scheduler.setQueueLocked(false);
			return new CommandResponse("unlock", executor.getAsMention() + " unlocked the queue.");
		} else {
			scheduler.setQueueLocked(true);
			return new CommandResponse("lock", executor.getAsMention() + " locked the queue.");
		}
	}
}