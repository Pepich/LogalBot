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
import net.dv8tion.jda.core.entities.Member;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

public final class DataManager {
	private static final String host = System.getenv("REDIS_HOST");
	private static final String password = System.getenv("REDIS_AUTH");
	private static final String databaseNumber = System.getenv("REDIS_DATABASE_NUMBER");
	private static final Logger logger = LoggerFactory.getLogger(DataManager.class);
	private static Jedis jedis = new Jedis();

	public static void verifyConnection(){
		if (!jedis.isConnected()){
			jedis = new Jedis(host);

			if (password != null){
				jedis.auth(password);
			}

			if (databaseNumber != null){
				int num = Integer.parseInt(databaseNumber);
				jedis.select(num);
			}
		}
	}

	public static void runMigrations(){
		verifyConnection();

		if (jedis.get("schemaVersion") == null){
			logger.info("Migrating schema to version 1...");
			jedis.set("schemaVersion", "1");
			logger.info("Migration to schema version 1 complete.");
		}
	}

	public static String getUserValue(Member member, String key){
		verifyConnection();
		return jedis.get("g" + member.getGuild().getId() + ":u" + member.getUser().getId() + ":" + key);
	}

	public static void setUserValue(Member member, String key, String value){
		verifyConnection();
		jedis.set("g" + member.getGuild().getId() + ":u" + member.getUser().getId() + ":" + key, value);
	}

	public static String getGuildValue(Guild guild, String key){
		verifyConnection();
		return jedis.get("g" + guild.getId() + ":" + key);
	}

	public static void setGuildValue(Guild guild, String key, String value){
		verifyConnection();
		jedis.set("g" + guild.getId() + ":" + key, value);
	}

	public static void deleteUserKey(Member member, String key){
		verifyConnection();
		jedis.del("g" + member.getGuild().getId() + ":u" + member.getUser().getId() + ":" + key);
	}

	public static void deleteGuildKey(Guild guild, String key){
		verifyConnection();
		jedis.del("g" + guild.getId() + ":" + key);
	}
}