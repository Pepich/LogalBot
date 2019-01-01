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

package com.logaldeveloper.logalbot.commands.administration;

import com.logaldeveloper.logalbot.Main;
import com.logaldeveloper.logalbot.commands.Command;
import com.logaldeveloper.logalbot.commands.CommandResponse;
import com.logaldeveloper.logalbot.commands.PermissionManager;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

import java.util.concurrent.TimeUnit;

public final class Whitelist implements Command {
	@Override
	public CommandResponse execute(String[] arguments, User executor, TextChannel channel){
		if (!executor.equals(Main.getOwner())){
			return new CommandResponse("no_entry_sign", "Sorry " + executor.getAsMention() + ", but you are not allowed to use this command.").setDeletionDelay(10, TimeUnit.SECONDS);
		}

		if (arguments.length == 0){
			return new CommandResponse("no_entry_sign", "Sorry " + executor.getAsMention() + ", but you need to specify a user to add or remove from the whitelist.").setDeletionDelay(10, TimeUnit.SECONDS);
		}

		String userID = arguments[0].replaceFirst("<@[!]?([0-9]*)>", "$1");
		User user;
		try{
			user = channel.getJDA().getUserById(userID);
		} catch (Throwable exception){
			return new CommandResponse("no_entry_sign", "Sorry " + executor.getAsMention() + ", but that doesn't appear to be a valid user.").setDeletionDelay(10, TimeUnit.SECONDS);
		}

		if (user == null){
			return new CommandResponse("no_entry_sign", "Sorry " + executor.getAsMention() + ", but that doesn't appear to be a valid user.").setDeletionDelay(10, TimeUnit.SECONDS);
		}

		if (user.equals(Main.getOwner())){
			return new CommandResponse("no_entry_sign", "Sorry " + executor.getAsMention() + ", but you are not allowed to remove that user from the whitelist.").setDeletionDelay(10, TimeUnit.SECONDS);
		}

		if (user.isBot()){
			return new CommandResponse("no_entry_sign", "Sorry " + executor.getAsMention() + ", but you cannot whitelist bots.").setDeletionDelay(10, TimeUnit.SECONDS);
		}

		Guild guild = channel.getGuild();
		if (PermissionManager.isWhitelisted(user, guild)){
			PermissionManager.removeFromWhitelist(user, guild);
			return new CommandResponse("heavy_multiplication_x", executor.getAsMention() + " has removed " + user.getAsMention() + " from the whitelist.");
		} else {
			PermissionManager.addToWhitelist(user, guild);
			return new CommandResponse("heavy_check_mark", executor.getAsMention() + " has added " + user.getAsMention() + " to the whitelist.");
		}
	}
}