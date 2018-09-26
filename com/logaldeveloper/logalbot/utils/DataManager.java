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

package com.logaldeveloper.logalbot.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.dv8tion.jda.core.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;

public class DataManager {
	private static final Logger logger = LoggerFactory.getLogger(DataManager.class);

	private static final String dataDirectory = "data";
	private static final HashMap<User, HashMap<String, String>> dataCache = new HashMap<>();

	private static void loadData(User user){
		File jsonFile = new File(dataDirectory + File.separator + user.getId() + ".json");

		if (jsonFile.exists()){
			logger.info("Loading data file for '" + user.getName() + "' from '" + jsonFile.getAbsolutePath() + "'...");
			try (Reader reader = new FileReader(jsonFile.getAbsolutePath())){
				Gson gson = new GsonBuilder().create();
				dataCache.put(user, gson.fromJson(reader, new TypeToken<HashMap<String, String>>() {
				}.getType()));
			} catch (IOException exception){
				logger.error("An error occured while loading the data file at '" + jsonFile.getAbsolutePath() + "'!");
				exception.printStackTrace();
				return;
			}
			logger.info("Load complete.");
		} else {
			logger.info("Creating data file for '" + user.getName() + "' at '" + jsonFile.getAbsolutePath() + "'...");
			dataCache.put(user, new HashMap<>());
			saveData(user);
		}
	}

	private static void saveData(User user){
		File jsonFile = new File(dataDirectory + File.separator + user.getId() + ".json");

		logger.info("Saving data file for '" + user.getName() + "' at '" + jsonFile.getAbsolutePath() + "'...");
		try (Writer writer = new FileWriter(jsonFile.getAbsolutePath())){
			Gson gson = new GsonBuilder().create();
			gson.toJson(dataCache.get(user), writer);
		} catch (IOException exception){
			logger.error("An error occured while saving the data file at '" + jsonFile.getAbsolutePath() + "'!");
			exception.printStackTrace();
			return;
		}
		logger.info("Save complete.");
	}

	public static String getValue(User user, String key){
		if (!dataCache.containsKey(user)){
			loadData(user);
		}

		return dataCache.get(user).get(key);
	}

	public static void setValue(User user, String key, String value){
		if (!dataCache.containsKey(user)){
			loadData(user);
		}

		dataCache.get(user).put(key, value);

		saveData(user);
	}
}