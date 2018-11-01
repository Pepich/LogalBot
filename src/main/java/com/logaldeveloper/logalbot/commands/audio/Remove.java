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
import com.logaldeveloper.logalbot.audio.TrackScheduler;
import com.logaldeveloper.logalbot.commands.Command;
import com.logaldeveloper.logalbot.utils.AudioUtil;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

public class Remove implements Command {
	@Override
	public void initialize(){
	}

	@Override
	public String execute(String[] arguments, User executor, TextChannel channel){
		if (!AudioUtil.isAllowedChannelForAudioCommands(channel)){
			return ":no_entry_sign: Sorry " + executor.getAsMention() + ", but audio commands can only be used in text channels named `" + Main.getTextChannelNameForAudioCommands() + "`.";
		}

		if (arguments.length == 0){
			return ":no_entry_sign: Sorry " + executor.getAsMention() + ", but you must provide an index.";
		}

		int index;
		try{
			index = Integer.parseInt(arguments[0]);
		} catch (NumberFormatException exception){
			return ":no_entry_sign: Sorry " + executor.getAsMention() + ", but the index must be a number.";
		}

		try{
			AudioTrack trackToBeRemoved = TrackScheduler.getQueue().get(index - 1);
			TrackScheduler.removeFromQueue(index - 1);
			return ":scissors: " + executor.getAsMention() + " removed **" + trackToBeRemoved.getInfo().title + "** from the queue.";
		} catch (IndexOutOfBoundsException exception){
			return ":no_entry_sign: Sorry " + executor.getAsMention() + ", but that index is outside the bounds of the queue.";
		}
	}
}