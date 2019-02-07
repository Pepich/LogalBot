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

import com.logaldeveloper.logalbot.commands.Command;
import com.logaldeveloper.logalbot.commands.CommandResponse;
import com.logaldeveloper.logalbot.commands.PermissionManager;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.concurrent.TimeUnit;

public final class Whitelist implements Command {
	@Override
	public CommandResponse execute(String[] arguments, Member executor, TextChannel channel){
		Guild guild = channel.getGuild();
		if (!executor.hasPermission(Permission.ADMINISTRATOR)){
			return new CommandResponse("no_entry_sign", "Sorry " + executor.getAsMention() + ", but you are not allowed to use this command.").setDeletionDelay(10, TimeUnit.SECONDS);
		}

		if (arguments.length == 0){
			return new CommandResponse("no_entry_sign", "Sorry " + executor.getAsMention() + ", but you need to specify a user to add or remove from the whitelist.").setDeletionDelay(10, TimeUnit.SECONDS);
		}

		String userID = arguments[0].replaceFirst("<@[!]?([0-9]*)>", "$1");
		Member member;
		try{
			member = guild.getMemberById(userID);
		} catch (Throwable exception){
			return new CommandResponse("no_entry_sign", "Sorry " + executor.getAsMention() + ", but that doesn't appear to be a valid user.").setDeletionDelay(10, TimeUnit.SECONDS);
		}

		if (member == null){
			return new CommandResponse("no_entry_sign", "Sorry " + executor.getAsMention() + ", but that doesn't appear to be a valid user.").setDeletionDelay(10, TimeUnit.SECONDS);
		}

		if (member.hasPermission(Permission.ADMINISTRATOR)){
			return new CommandResponse("no_entry_sign", "Sorry " + executor.getAsMention() + ", but you cannot remove that user from the whitelist due to them being a guild administrator.").setDeletionDelay(10, TimeUnit.SECONDS);
		}

		if (member.getUser().isBot()){
			return new CommandResponse("no_entry_sign", "Sorry " + executor.getAsMention() + ", but you cannot whitelist bots.").setDeletionDelay(10, TimeUnit.SECONDS);
		}

		if (PermissionManager.isWhitelisted(member)){
			PermissionManager.removeFromWhitelist(member);
			return new CommandResponse("heavy_multiplication_x", executor.getAsMention() + " has removed " + member.getAsMention() + " from the whitelist.");
		} else {
			PermissionManager.addToWhitelist(member);
			return new CommandResponse("heavy_check_mark", executor.getAsMention() + " has added " + member.getAsMention() + " to the whitelist.");
		}
	}
}