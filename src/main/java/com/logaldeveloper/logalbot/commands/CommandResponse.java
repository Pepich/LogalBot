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

package com.logaldeveloper.logalbot.commands;

import com.logaldeveloper.logalbot.tasks.MessageDeleteTask;
import com.logaldeveloper.logalbot.utils.Scheduler;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.concurrent.TimeUnit;

public class CommandResponse {
	private String emoji;
	private String response;

	private long deletionDelay = 0;
	private TimeUnit deletionDelayUnit;

	public CommandResponse(String emoji, String response){
		this.emoji = emoji;
		this.response = response;
	}

	public CommandResponse setDeletionDelay(long delay, TimeUnit unit){
		this.deletionDelay = delay;
		this.deletionDelayUnit = unit;
		return this;
	}

	public void sendResponse(TextChannel channel){
		Message responseMessage = channel.sendMessage(":" + emoji + ": " + response).complete();

		if ((deletionDelay != 0) && (deletionDelayUnit != null)){
			Scheduler.schedule(new MessageDeleteTask(responseMessage), deletionDelay, deletionDelayUnit);
		}
	}
}