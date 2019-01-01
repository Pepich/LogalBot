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
import com.logaldeveloper.logalbot.utils.TrackUtil;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

public final class Queue implements Command {
	@Override
	public CommandResponse execute(String[] arguments, User executor, TextChannel channel){
		Guild guild = channel.getGuild();
		if (AudioUtil.getTrackScheduler(guild).isQueueEmpty()){
			return new CommandResponse("information_source", executor.getAsMention() + ", the queue is empty.");
		}

		CommandResponse response = new CommandResponse("bookmark_tabs", executor.getAsMention() + ", the following tracks are in the queue:");
		response.attachEmbed(TrackUtil.generateTrackListInfoEmbed(AudioUtil.getTrackScheduler(guild).getQueue()));
		return response;
	}
}