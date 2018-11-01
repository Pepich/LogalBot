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

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;

public class CommandManager {
	private static final HashMap<String, Command> commandMap = new HashMap<>();
	private static final HashMap<String, Boolean> permissionMap = new HashMap<>();

	private static final Logger logger = LoggerFactory.getLogger(CommandManager.class);

	public static void executeCommand(String[] command, User executor, TextChannel channel, Message message){
		String commandName = command[0].toLowerCase();
		String[] arguments = Arrays.copyOfRange(command, 1, command.length);
		if (!commandMap.containsKey(commandName)){
			return;
		}

		String usageLog = executor.getName() + " executed command '" + commandName + "' with arguments '" + String.join(" ", arguments) + "'";
		message.delete().reason("A valid LogalBot command was executed.").queue();

		if (permissionMap.get(commandName) && !PermissionManager.isWhitelisted(executor)){
			logger.info(usageLog + ", but was denied usage access due to not being on the whitelist.");
			channel.sendMessage(":no_entry_sign: Sorry " + executor.getAsMention() + ", but you are not allowed to use this command.").queue();
			return;
		}

		String response;
		try{
			response = commandMap.get(commandName).execute(arguments, executor, channel);
		} catch (Throwable exception){
			logger.warn(usageLog + ", but an error occurred while executing the command!");
			response = ":sos: Sorry " + executor.getAsMention() + ", but an error occurred while executing your command!";
			exception.printStackTrace();
		}

		if (response == null){
			logger.warn(usageLog + ", but the command did not return a response!");
			response = ":sos: Sorry " + executor.getAsMention() + ", but an error occurred while executing your command!";
		}

		logger.info(usageLog + ".");

		if (!response.equals("")){
			channel.sendMessage(response).queue();
		}
	}

	public static void registerCommand(String command, Command commandObject, boolean mustBeWhitelisted){
		commandObject.initialize();
		commandMap.put(command, commandObject);
		permissionMap.put(command, mustBeWhitelisted);
	}

	public static void reinitializeCommand(String command){
		commandMap.get(command).initialize();
	}
}