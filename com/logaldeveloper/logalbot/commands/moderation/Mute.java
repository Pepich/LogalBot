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

package com.logaldeveloper.logalbot.commands.moderation;

import com.logaldeveloper.logalbot.Main;
import com.logaldeveloper.logalbot.commands.Command;
import com.logaldeveloper.logalbot.commands.PermissionManager;
import com.logaldeveloper.logalbot.utils.DataManager;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.requests.restaction.PermissionOverrideAction;

public class Mute implements Command {
	@Override
	public void initialize(){
	}

	@Override
	public String execute(String[] arguments, User executor, TextChannel channel){
		if (arguments.length == 0){
			return ":no_entry_sign: Sorry " + executor.getAsMention() + ", but you need to specify a user to mute.";
		}

		String userID = arguments[0].replaceFirst("!", "").replaceFirst("<@(.*?)>", "$1");
		User user;
		try{
			user = Main.getJDA().getUserById(userID);
		} catch (Throwable exception){
			return ":no_entry_sign: Sorry " + executor.getAsMention() + ", but that doesn't appear to be a valid user.";
		}

		if (PermissionManager.isWhitelisted(user)){
			return ":no_entry_sign: Sorry " + executor.getAsMention() + ", but you are not allowed to mute that user.";
		}

		if (DataManager.getValueOrUseDefault(user, "muted", "false").equals("false")){
			DataManager.setValue(user, "muted", "true");
			for (TextChannel textChannel : Main.getGuild().getTextChannels()){
				PermissionOverrideAction overrides = textChannel.createPermissionOverride(Main.getGuild().getMember(user));
				overrides.setDeny(448576);
				overrides.queue();
			}
			return ":zipper_mouth: " + executor.getAsMention() + " has muted " + user.getAsMention() + " globally for an indefinite amount of time.";
		} else {
			return ":no_entry_sign: Sorry " + executor.getAsMention() + ", but that user is already muted.";
		}
	}
}