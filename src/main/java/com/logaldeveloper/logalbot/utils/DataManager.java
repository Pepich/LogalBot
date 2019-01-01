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

package com.logaldeveloper.logalbot.utils;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import redis.clients.jedis.Jedis;

public final class DataManager {
	private static final String host = System.getenv("REDIS_HOST");
	private static final String password = System.getenv("REDIS_AUTH");

	private static Jedis jedis = new Jedis();

	private static void verifyConnection(){
		if (!jedis.isConnected()){
			jedis = new Jedis(host);

			if (password != null){
				jedis.auth(password);
			}
		}
	}

	public static String getUserValue(User user, Guild guild, String key){
		verifyConnection();
		return jedis.get("g" + guild.getId() + ":u" + user.getId() + ":" + key);
	}

	public static void setUserValue(User user, Guild guild, String key, String value){
		verifyConnection();
		jedis.set("g" + guild.getId() + ":u" + user.getId() + ":" + key, value);
	}

	public static String getGuildValue(Guild guild, String key){
		verifyConnection();
		return jedis.get("g" + guild.getId() + ":" + key);
	}

	public static void setGuildValue(Guild guild, String key, String value){
		verifyConnection();
		jedis.set("g" + guild.getId() + ":" + key, value);
	}
}