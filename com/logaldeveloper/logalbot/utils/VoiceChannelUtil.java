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

import com.logaldeveloper.logalbot.Main;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.VoiceChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class VoiceChannelUtil {
	private static final Logger logger = LoggerFactory.getLogger(VoiceChannelUtil.class);

	private static ArrayList<String> getChannelMembersAsIDs(VoiceChannel channel){
		ArrayList<String> members = new ArrayList<>();
		for (Member member : channel.getMembers()){
			members.add(member.getUser().getId());
		}
		return members;
	}

	public static VoiceChannel getCurrentVoiceChannelFromUser(User user){
		for (VoiceChannel channel : Main.getGuild().getVoiceChannels()){
			if (VoiceChannelUtil.getChannelMembersAsIDs(channel).contains(user.getId())){
				return channel;
			}
		}
		return null;
	}

	public static VoiceChannel getCurrentVoiceChannel(){
		return AudioUtil.getCurrentVoiceChannel();
	}

	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	public static boolean isInCurrentVoiceChannel(User user){
		return VoiceChannelUtil.getChannelMembersAsIDs(VoiceChannelUtil.getCurrentVoiceChannel()).contains(user.getId());
	}

	public static void joinVoiceChannel(VoiceChannel channel){
		if (!channel.equals(getCurrentVoiceChannel())){
			VoiceChannelUtil.leaveCurrentVoiceChannel();
			logger.info("Connecting to voice channel '" + channel.getName() + "'.");
			AudioUtil.openAudioConnection(channel);
		}
	}

	public static void leaveCurrentVoiceChannel(){
		if (VoiceChannelUtil.isConnectedToVoiceChannel()){
			logger.info("Disconnecting from voice channel '" + VoiceChannelUtil.getCurrentVoiceChannel().getName() + "'.");
			AudioUtil.closeAudioConnection();
		}
	}

	public static boolean isConnectedToVoiceChannel(){
		return AudioUtil.isAudioConnectionOpen();
	}
}