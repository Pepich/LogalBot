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

package com.logaldeveloper.logalbot.commands;

import com.logaldeveloper.logalbot.tasks.MessageDeleteTask;
import com.logaldeveloper.logalbot.utils.ReactionCallbackManager;
import com.logaldeveloper.logalbot.utils.Scheduler;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public final class CommandResponse {
	private final String emoji;
	private final String response;
	private MessageEmbed responseEmbed;

	private LinkedHashMap<String, ReactionCallback> callbacks = new LinkedHashMap<>();
	private User callbacksTarget;

	private long deletionDelay = 0;
	private TimeUnit deletionDelayUnit;

	public CommandResponse(String emoji, String response){
		this.emoji = emoji;
		this.response = response;
	}

	public CommandResponse attachEmbed(MessageEmbed embed){
		this.responseEmbed = embed;
		return this;
	}

	public CommandResponse setDeletionDelay(long delay, TimeUnit unit){
		this.deletionDelay = delay;
		this.deletionDelayUnit = unit;
		return this;
	}

	public CommandResponse addReactionCallback(String emoji, ReactionCallback callback){
		this.callbacks.put(emoji, callback);
		return this;
	}

	public CommandResponse setReactionCallbackTarget(User user){
		this.callbacksTarget = user;
		return this;
	}

	public void sendResponse(TextChannel channel){
		MessageBuilder builder = new MessageBuilder();
		builder.setContent(":" + this.emoji + ": " + this.response);

		if (this.responseEmbed != null){
			builder.setEmbed(this.responseEmbed);
		}

		channel.sendMessage(builder.build()).queue(this::handleResponseCreation);
	}

	private void handleResponseCreation(Message message){
		if ((this.deletionDelay != 0) && (this.deletionDelayUnit != null)){
			Scheduler.schedule(new MessageDeleteTask(message), this.deletionDelay, this.deletionDelayUnit);
		}

		for (Map.Entry<String, ReactionCallback> callback : callbacks.entrySet()){
			ReactionCallbackManager.registerCallback(message.getId(), callback.getKey(), callback.getValue());
			ReactionCallbackManager.setCallbackTarget(this.callbacksTarget, message.getId());
			message.addReaction(callback.getKey()).queue();
		}
	}
}