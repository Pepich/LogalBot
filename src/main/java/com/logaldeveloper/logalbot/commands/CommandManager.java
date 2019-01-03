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

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public final class CommandManager {
	private static final HashMap<String, Command> commandMap = new HashMap<>();
	private static final HashMap<String, Boolean> permissionMap = new HashMap<>();

	private static final Logger logger = LoggerFactory.getLogger(CommandManager.class);

	public static void executeCommand(String[] command, User executor, TextChannel channel){
		String commandName = command[0].toLowerCase();
		String[] arguments = Arrays.copyOfRange(command, 1, command.length);
		Guild guild = channel.getGuild();
		CommandResponse response;

		logger.info(executor.getName() + " (" + executor.getId() + ") executed command '" + commandName + "' with arguments '" + String.join(" ", arguments) + "' in " + guild.getName() + " (" + guild.getId() + ").");
		if (!commandMap.containsKey(commandName)){
			response = new CommandResponse("question", "Sorry " + executor.getAsMention() + ", but I do not know what that command is.");
			response.setDeletionDelay(10, TimeUnit.SECONDS);
			response.sendResponse(channel);
			return;
		}

		if (permissionMap.get(commandName) && !PermissionManager.isWhitelisted(executor, channel.getGuild())){
			logger.info(executor.getName() + " (" + executor.getId() + ") was denied access to a command due to not being whitelisted in " + guild.getName() + " (" + guild.getId() + ").");
			response = new CommandResponse("no_entry_sign", "Sorry " + executor.getAsMention() + ", but you are not allowed to use this command.");
			response.setDeletionDelay(10, TimeUnit.SECONDS);
			response.sendResponse(channel);
			return;
		}

		try{
			response = commandMap.get(commandName).execute(arguments, executor, channel);
		} catch (Throwable exception){
			logger.info("An error occured while executing a command for " + executor.getName() + " (" + executor.getId() + ") in " + guild.getName() + " (" + guild.getId() + ").");
			exception.printStackTrace();
			response = new CommandResponse("sos", "Sorry " + executor.getAsMention() + ", but an error occurred while executing your command! Please contact LogalDeveloper about this!");
			response.setDeletionDelay(10, TimeUnit.SECONDS);
			response.sendResponse(channel);
			return;
		}

		if (response != null){
			response.sendResponse(channel);
		}
	}

	public static void registerCommand(String command, Command commandObject, boolean mustBeWhitelisted){
		commandMap.put(command, commandObject);
		permissionMap.put(command, mustBeWhitelisted);
	}
}