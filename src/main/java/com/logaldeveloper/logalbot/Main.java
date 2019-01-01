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

package com.logaldeveloper.logalbot;

import com.logaldeveloper.logalbot.commands.CommandManager;
import com.logaldeveloper.logalbot.commands.administration.Whitelist;
import com.logaldeveloper.logalbot.commands.audio.*;
import com.logaldeveloper.logalbot.commands.fun.EightBall;
import com.logaldeveloper.logalbot.commands.general.About;
import com.logaldeveloper.logalbot.commands.general.Help;
import com.logaldeveloper.logalbot.events.GuildMessageReceived;
import com.logaldeveloper.logalbot.events.GuildVoiceLeave;
import com.logaldeveloper.logalbot.events.GuildVoiceMove;
import com.logaldeveloper.logalbot.utils.AudioUtil;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;

public class Main {
	private static final String token = System.getenv("TOKEN");
	private static final String ownerUserID = System.getenv("OWNER_USER_ID");
	private static final String textChannelNameForAudioCommands = System.getenv("AUDIO_COMMANDS_TEXT_CHANNEL");

	private static final Logger logger = LoggerFactory.getLogger(Main.class);
	private static JDA jda;
	private static User owner;

	public static void main(String[] arguments){
		logger.info("Beginning setup of LogalBot...");

		logger.info("Attempting to log into Discord...");
		try{
			JDABuilder jdaBuilder = new JDABuilder(AccountType.BOT);
			jdaBuilder.setAutoReconnect(true);
			jdaBuilder.setAudioEnabled(true);
			jdaBuilder.setToken(token);
			jda = jdaBuilder.buildBlocking();
		} catch (LoginException exception){
			logger.error("The token specified is not valid.");
			System.exit(1);
		} catch (Throwable exception){
			logger.error("An error occured while attempting to set up JDA!");
			exception.printStackTrace();
			System.exit(1);
		}
		logger.info("Successfully logged into Discord as bot user '" + jda.getSelfUser().getName() + "'.");

		logger.info("Attempting to find owner user with user ID '" + ownerUserID + "'...");
		owner = jda.getUserById(ownerUserID);
		if (owner == null){
			logger.error("Unable to find owner user! Either the owner is not a member of any guild the bot is a member of, or the user ID specified is invalid!");
			System.exit(1);
		}
		logger.info("Owner is '" + owner.getName() + "'.");

		logger.info("Beginning initialization of LogalBot...");
		AudioUtil.initializePlayerManager();

		logger.info("Registering events...");
		jda.addEventListener(new GuildVoiceLeave());
		jda.addEventListener(new GuildVoiceMove());

		logger.info("Registering commands...");
		// General Commands
		CommandManager.registerCommand("about", new About(), false);
		CommandManager.registerCommand("help", new Help(), false);

		// Fun Commands
		CommandManager.registerCommand("8ball", new EightBall(), false);

		// Audio Commands
		CommandManager.registerCommand("forceskip", new ForceSkip(), true);
		CommandManager.registerCommand("lock", new Lock(), true);
		CommandManager.registerCommand("nowplaying", new NowPlaying(), false);
		CommandManager.registerCommand("pause", new Pause(), true);
		CommandManager.registerCommand("play", new Play(), false);
		CommandManager.registerCommand("queue", new Queue(), false);
		CommandManager.registerCommand("remove", new Remove(), true);
		CommandManager.registerCommand("reset", new Reset(), true);
		CommandManager.registerCommand("skip", new Skip(), false);
		CommandManager.registerCommand("volume", new Volume(), true);

		// Administration Commands
		CommandManager.registerCommand("whitelist", new Whitelist(), true);

		logger.info("Everything seems to be ready! Enabling command listener...");
		jda.addEventListener(new GuildMessageReceived());
		logger.info("Initialization complete!");
	}

	public static String getTextChannelNameForAudioCommands(){
		return textChannelNameForAudioCommands;
	}

	public static User getOwner(){
		return owner;
	}
}