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

package com.logaldeveloper.logalbot.commands.administration;

import com.logaldeveloper.logalbot.Main;
import com.logaldeveloper.logalbot.commands.Command;
import com.logaldeveloper.logalbot.commands.PermissionManager;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

public class Whitelist implements Command {
	@Override
	public void initialize(){
		PermissionManager.loadWhitelistFile();
		PermissionManager.addToWhitelist(Main.getOwner());
	}

	@Override
	public String execute(String[] arguments, User executor, TextChannel channel){
		if (arguments.length == 0){
			StringBuilder reply = new StringBuilder(":bookmark_tabs: " + executor.getAsMention() + ", the following users are on the whitelist:\n");
			for (String userID : PermissionManager.getWhitelistedUsers()){
				reply.append(":arrow_right: ").append(Main.getJDA().getUserById(userID).getAsMention()).append("\n");
			}
			return reply.toString();
		}

		String userID = arguments[0].replaceFirst("!", "").replaceFirst("<@(.*?)>", "$1");
		User user;
		try{
			user = Main.getJDA().getUserById(userID);
		} catch (Throwable exception){
			return ":no_entry_sign: Sorry " + executor.getAsMention() + ", but that doesn't appear to be a valid user.";
		}


		if (user == null){
			return ":no_entry_sign: Sorry " + executor.getAsMention() + ", but that doesn't appear to be a valid user.";
		}

		if (user.equals(Main.getOwner())){
			return ":no_entry_sign: Sorry " + executor.getAsMention() + ", but you are not allowed to remove that user from the whitelist.";
		}

		if (user.isBot()){
			return ":no_entry_sign: Sorry " + executor.getAsMention() + ", but you cannot whitelist bots.";
		}

		if (PermissionManager.isWhitelisted(user)){
			PermissionManager.removeFromWhitelist(user);
			return ":heavy_multiplication_x: " + executor.getAsMention() + " has removed " + user.getAsMention() + " from the whitelist.";
		} else {
			PermissionManager.addToWhitelist(user);
			return ":heavy_check_mark: " + executor.getAsMention() + " has added " + user.getAsMention() + " to the whitelist.";
		}
	}
}