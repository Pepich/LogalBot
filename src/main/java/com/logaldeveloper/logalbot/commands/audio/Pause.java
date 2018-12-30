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
import com.logaldeveloper.logalbot.utils.VoiceChannelUtil;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

import java.util.concurrent.TimeUnit;

public class Pause implements Command {
	@Override
	public void initialize(){
		AudioUtil.setPausedState(false);
	}

	@Override
	public CommandResponse execute(String[] arguments, User executor, TextChannel channel){
		if (!AudioUtil.isAllowedChannelForAudioCommands(channel)){
			return new CommandResponse("no_entry_sign", "Sorry " + executor.getAsMention() + ", but audio commands can only be used in text channels named `" + Main.getTextChannelNameForAudioCommands() + "`.").setDeletionDelay(10, TimeUnit.SECONDS);
		}

		if (!AudioUtil.isTrackLoaded()){
			return new CommandResponse("no_entry_sign", "Sorry " + executor.getAsMention() + ", but there must be a track playing in order to pause or resume the player.").setDeletionDelay(10, TimeUnit.SECONDS);
		}

		if (!VoiceChannelUtil.isInCurrentVoiceChannel(executor)){
			return new CommandResponse("no_entry_sign", "Sorry " + executor.getAsMention() + ", but you must be in voice channel `" + AudioUtil.getCurrentVoiceChannel().getName() + "` in order to pause or unplause the player.").setDeletionDelay(10, TimeUnit.SECONDS);
		}

		if (AudioUtil.isPlayerPaused()){
			AudioUtil.setPausedState(false);
			return new CommandResponse("arrow_forward", executor.getAsMention() + " resumed the track player.");
		} else {
			AudioUtil.setPausedState(true);
			return new CommandResponse("pause_button", executor.getAsMention() + " paused the track player.");
		}
	}
}