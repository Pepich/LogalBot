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
import com.logaldeveloper.logalbot.utils.AudioUtil;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

import java.util.concurrent.TimeUnit;

public class Volume implements Command {
	@Override
	public void initialize(){
	}

	@Override
	public CommandResponse execute(String[] arguments, User executor, TextChannel channel){
		if (!AudioUtil.isAllowedChannelForAudioCommands(channel)){
			return new CommandResponse("no_entry_sign", "Sorry " + executor.getAsMention() + ", but audio commands can only be used in text channels named `" + Main.getTextChannelNameForAudioCommands() + "`.").setDeletionDelay(10, TimeUnit.SECONDS);
		}

		Guild guild = channel.getGuild();
		if (arguments.length == 0){
			if (AudioUtil.getVolume(guild) >= 75){
				return new CommandResponse("loud_sound", executor.getAsMention() + ", the volume is currently set to `" + AudioUtil.getVolume(guild) + "%`.");
			} else {
				return new CommandResponse("sound", executor.getAsMention() + ", the volume is currently set to `" + AudioUtil.getVolume(guild) + "%`.");
			}
		}

		int volume;
		try{
			volume = Integer.parseInt(arguments[0]);
		} catch (NumberFormatException exception){
			return new CommandResponse("no_entry_sign", "Sorry " + executor.getAsMention() + ", but the volume must be an integer.").setDeletionDelay(10, TimeUnit.SECONDS);
		}

		if (volume <= 150 && volume >= 1){
			int oldVolume = AudioUtil.getVolume(guild);
			AudioUtil.setVolume(guild, volume);
			if (volume >= 75){
				return new CommandResponse("loud_sound", executor.getAsMention() + " set the volume from `" + oldVolume + "%` to `" + volume + "%`.");
			} else {
				return new CommandResponse("sound", executor.getAsMention() + " set the volume from `" + oldVolume + "%` to `" + volume + "%`.");
			}
		} else {
			return new CommandResponse("no_entry_sign", "Sorry " + executor.getAsMention() + ", but the volume must be between 1% and 150%.").setDeletionDelay(10, TimeUnit.SECONDS);
		}
	}
}