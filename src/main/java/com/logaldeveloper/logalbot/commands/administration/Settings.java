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
import com.logaldeveloper.logalbot.utils.DataManager;
import com.logaldeveloper.logalbot.utils.StringUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.concurrent.TimeUnit;

public final class Settings implements Command {
	@Override
	public CommandResponse execute(String[] arguments, Member executor, TextChannel channel){
		Guild guild = executor.getGuild();

		if (arguments.length == 0){
			CommandResponse response = new CommandResponse("tools", executor.getAsMention() + ", these are the current settings for this guild:");
			EmbedBuilder builder = new EmbedBuilder();
			builder.setTitle("**Current Settings for " + StringUtil.sanitize(guild.getName()) + "**");

			String commandCharacter = DataManager.getGuildValue(guild, "commandCharacter");
			if (commandCharacter == null){
				builder.addField("Command Character", "Not Set", true);
			} else {
				builder.addField("Command Character", commandCharacter, true);
			}
			response.attachEmbed(builder.build());
			return response;
		}

		if (arguments[0].equalsIgnoreCase("commandcharacter")){
			if (arguments.length == 1){
				DataManager.deleteGuildKey(guild, "commandCharacter");
				return new CommandResponse("white_check_mark", executor.getAsMention() + ", the command character has been disabled.");
			} else {
				String commandCharacter = arguments[1];
				if (commandCharacter.length() > 1){
					return new CommandResponse("no_entry_sign", "Sorry " + executor.getAsMention() + ", but the command character must be a single character.").setDeletionDelay(10, TimeUnit.SECONDS);
				} else {
					DataManager.setGuildValue(guild, "commandCharacter", commandCharacter);
					return new CommandResponse("white_check_mark", executor.getAsMention() + ", the command character has been set to `" + commandCharacter + "`.");
				}
			}
		} else {
			return new CommandResponse("no_entry_sign", "Sorry " + executor.getAsMention() + ", but I do not know what that setting is.").setDeletionDelay(10, TimeUnit.SECONDS);
		}
	}
}
