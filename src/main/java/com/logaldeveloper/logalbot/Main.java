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

package com.logaldeveloper.logalbot;

import com.logaldeveloper.logalbot.commands.CommandManager;
import com.logaldeveloper.logalbot.commands.administration.Settings;
import com.logaldeveloper.logalbot.commands.administration.Whitelist;
import com.logaldeveloper.logalbot.commands.audio.*;
import com.logaldeveloper.logalbot.commands.fun.EightBall;
import com.logaldeveloper.logalbot.commands.general.About;
import com.logaldeveloper.logalbot.commands.general.Help;
import com.logaldeveloper.logalbot.events.*;
import com.logaldeveloper.logalbot.utils.AudioUtil;
import com.logaldeveloper.logalbot.utils.DataManager;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;

public final class Main {
	private static final String token = System.getenv("TOKEN");

	private static final Logger logger = LoggerFactory.getLogger(Main.class);

	public static void main(String[] arguments){
		logger.info("Beginning setup of LogalBot...");

		logger.info("Verifying connection to Redis...");
		try{
			DataManager.verifyConnection();
		} catch (Throwable exception){
			logger.error("An error occurred while attempting to verify the connection to Redis!", exception);
			System.exit(1);
		}

		logger.info("Running any needed schema migrations...");
		try{
			DataManager.runMigrations();
		} catch (Throwable exception){
			logger.error("An error occurred while attempting to migrate the database!", exception);
			System.exit(1);
		}

		logger.info("Attempting to log into Discord...");
		JDA jda = null;
		try{
			JDABuilder jdaBuilder = new JDABuilder(AccountType.BOT);
			jdaBuilder.setAutoReconnect(true);
			jdaBuilder.setAudioEnabled(true);
			jdaBuilder.setToken(token);
			jdaBuilder.addEventListener(new GuildReady());
			jda = jdaBuilder.build().awaitReady();
		} catch (LoginException exception){
			logger.error("The token specified is not valid.");
			System.exit(1);
		} catch (Throwable exception){
			logger.error("An error occurred while attempting to set up JDA!");
			exception.printStackTrace();
			System.exit(1);
		}
		logger.info("Successfully logged into Discord as bot user '" + jda.getSelfUser().getName() + "'.");

		logger.info("Beginning initialization of LogalBot...");
		AudioUtil.initializePlayerManager();

		logger.info("Registering events...");
		jda.addEventListener(new GuildJoin());
		jda.addEventListener(new GuildVoiceLeave());
		jda.addEventListener(new GuildVoiceMove());
		jda.addEventListener(new GuildMessageReactionAdd());

		logger.info("Registering commands...");
		// General Commands
		CommandManager.registerCommand("about", new About(), false);
		CommandManager.registerCommand("help", new Help(), false);

		// Fun Commands
		CommandManager.registerCommand("8ball", new EightBall(), false);

		// Audio Commands
		CommandManager.registerCommand("forceskip", new ForceSkip(), true);
		CommandManager.registerCommandAlias("fs", "forceskip");
		CommandManager.registerCommand("lock", new Lock(), true);
		CommandManager.registerCommandAlias("l", "lock");
		CommandManager.registerCommand("nowplaying", new NowPlaying(), false);
		CommandManager.registerCommandAlias("np", "nowplaying");
		CommandManager.registerCommand("pause", new Pause(), true);
		CommandManager.registerCommand("play", new Play(), false);
		CommandManager.registerCommandAlias("p", "play");
		CommandManager.registerCommandAlias("add", "play");
		CommandManager.registerCommandAlias("a", "play");
		CommandManager.registerCommand("queue", new Queue(), false);
		CommandManager.registerCommandAlias("q", "queue");
		CommandManager.registerCommand("remove", new Remove(), true);
		CommandManager.registerCommandAlias("r", "remove");
		CommandManager.registerCommandAlias("x", "remove");
		CommandManager.registerCommand("reset", new Reset(), true);
		CommandManager.registerCommand("skip", new Skip(), false);
		CommandManager.registerCommandAlias("s", "skip");
		CommandManager.registerCommand("volume", new Volume(), true);
		CommandManager.registerCommandAlias("v", "volume");
		CommandManager.registerCommand("shuffle", new Shuffle(), true);

		// Administration Commands
		CommandManager.registerCommand("whitelist", new Whitelist(), true);
		CommandManager.registerCommandAlias("wl", "whitelist");
		CommandManager.registerCommand("settings", new Settings(), true);
		CommandManager.registerCommandAlias("set", "settings");
		CommandManager.registerCommandAlias("configure", "settings");
		CommandManager.registerCommandAlias("config", "settings");

		logger.info("Everything seems to be ready! Enabling command listener...");
		jda.addEventListener(new GuildMessageReceived());
		logger.info("Initialization complete!");
	}
}