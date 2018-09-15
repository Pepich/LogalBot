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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.logaldeveloper.logalbot.Main;
import net.dv8tion.jda.core.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;

public class PermissionManager {
	private static final Logger logger = LoggerFactory.getLogger(PermissionManager.class);
	private static ArrayList<String> whitelistedUsers;

	public static boolean isWhitelisted(User user){
		return whitelistedUsers.contains(user.getId());
	}

	public static ArrayList<String> getWhitelistedUsers(){
		return whitelistedUsers;
	}

	public static void addToWhitelist(User user){
		if (!whitelistedUsers.contains(user.getId())){
			logger.info("'" + user.getName() + "' was added to the whitelist.");
			whitelistedUsers.add(user.getId());
			PermissionManager.saveWhitelistFile();
		}
	}

	public static void removeFromWhitelist(User user){
		logger.info("'" + user.getName() + "' was removed from the whitelist.");
		whitelistedUsers.remove(user.getId());
		PermissionManager.saveWhitelistFile();
	}

	public static void loadWhitelistFile(){
		File jsonFile = new File(Main.getWhitelistedUsersFile());

		if (jsonFile.exists()){
			logger.info("Loading whitelisted users from '" + Main.getWhitelistedUsersFile() + "'...");
			try (Reader reader = new FileReader((Main.getWhitelistedUsersFile()))){
				Gson gson = new GsonBuilder().create();
				whitelistedUsers = gson.fromJson(reader, new TypeToken<ArrayList<String>>() {
				}.getType());
			} catch (IOException exception){
				logger.error("An error occured while loading the whitelisted users file at '" + Main.getWhitelistedUsersFile() + "'!");
				exception.printStackTrace();
				return;
			}
			logger.info("Load complete.");
		} else {
			logger.info("Creating new whitelisted users file at '" + Main.getWhitelistedUsersFile() + "'...");
			whitelistedUsers = new ArrayList<>();
			PermissionManager.saveWhitelistFile();
		}
	}

	private static void saveWhitelistFile(){
		logger.info("Saving whitelisted users to '" + Main.getWhitelistedUsersFile() + "'...");
		try (Writer writer = new FileWriter((Main.getWhitelistedUsersFile()))){
			Gson gson = new GsonBuilder().create();
			gson.toJson(whitelistedUsers, writer);
		} catch (IOException exception){
			logger.error("An error occured while saving the whitelisted users file at '" + Main.getWhitelistedUsersFile() + "'!");
			exception.printStackTrace();
			return;
		}
		logger.info("Save complete.");
	}
}