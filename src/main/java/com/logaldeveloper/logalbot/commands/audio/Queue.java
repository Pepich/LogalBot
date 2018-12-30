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
import com.logaldeveloper.logalbot.commands.CommandResponse;
import com.logaldeveloper.logalbot.utils.AudioUtil;
import com.logaldeveloper.logalbot.utils.EmojiUtil;
import com.logaldeveloper.logalbot.utils.TimeUtil;
import com.logaldeveloper.logalbot.utils.VoiceChannelUtil;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

import java.util.concurrent.TimeUnit;

public class Queue implements Command {
	@Override
	public void initialize(){
	}

	@SuppressWarnings("ResultOfMethodCallIgnored")
	@Override
	public CommandResponse execute(String[] arguments, User executor, TextChannel channel){
		if (!AudioUtil.isAllowedChannelForAudioCommands(channel)){
			return new CommandResponse("no_entry_sign", "Sorry " + executor.getAsMention() + ", but audio commands can only be used in text channels named `" + Main.getTextChannelNameForAudioCommands() + "`.").setDeletionDelay(10, TimeUnit.SECONDS);
		}

		if (TrackScheduler.isQueueEmpty()){
			return new CommandResponse("information_source", executor.getAsMention() + ", the queue is empty.");
		}

		long time = 0;
		StringBuilder reply = new StringBuilder(executor.getAsMention() + ", the following tracks are in the queue:\n");
		for (int i = 0; i < 11; i++){
			try{
				TrackScheduler.getQueue().get(i); // Attempt to trigger an IndexOutOfBoundsException before we append to the string, otherwise we could get an incomplete track line added.
				reply.append(EmojiUtil.intToEmoji(i + 1)).append(" **").append(TrackScheduler.getQueue().get(i).getInfo().title).append("** (").append(TimeUtil.formatTime(TrackScheduler.getQueue().get(i).getDuration())).append(")\n");
				time += TrackScheduler.getQueue().get(i).getInfo().length;
			} catch (IndexOutOfBoundsException exception){
				break;
			}
		}
		reply.append(":clock130: Total play time: ").append(TimeUtil.formatTime(time));

		if (TrackScheduler.isQueueLocked()){
			reply.append("\n:lock: The queue is currently locked.");
		}

		if (!VoiceChannelUtil.isInCurrentVoiceChannel(executor)){
			reply.append("\n:headphones: You can listen to these tracks by joining voice channel `").append(AudioUtil.getCurrentVoiceChannel().getName()).append("`.");
		}

		return new CommandResponse("bookmark_tabs", reply.toString());
	}
}