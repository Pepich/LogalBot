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
import net.dv8tion.jda.core.entities.VoiceChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class VoiceChannelUtil {
	private static final Logger logger = LoggerFactory.getLogger(VoiceChannelUtil.class);

	public static VoiceChannel getCurrentVoiceChannelFromMember(Member member){
		for (VoiceChannel channel : member.getGuild().getVoiceChannels()){
			if (channel.getMembers().contains(member)){
				return channel;
			}
		}
		return null;
	}

	public static VoiceChannel getCurrentVoiceChannel(Guild guild){
		return AudioUtil.getCurrentVoiceChannel(guild);
	}

	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	public static boolean isInCurrentVoiceChannel(Member member){
		return VoiceChannelUtil.getCurrentVoiceChannel(member.getGuild()).getMembers().contains(member);
	}

	public static void joinVoiceChannel(VoiceChannel channel){
		if (!channel.equals(getCurrentVoiceChannel(channel.getGuild()))){
			VoiceChannelUtil.leaveCurrentVoiceChannel(channel.getGuild());
			logger.info("Connecting to voice channel '" + channel.getName() + "'.");
			AudioUtil.openAudioConnection(channel);
		}
	}

	public static void leaveCurrentVoiceChannel(Guild guild){
		if (VoiceChannelUtil.isConnectedToVoiceChannel(guild)){
			logger.info("Disconnecting from voice channel '" + VoiceChannelUtil.getCurrentVoiceChannel(guild).getName() + "'.");
			AudioUtil.closeAudioConnection(guild);
		}
	}

	public static boolean isConnectedToVoiceChannel(Guild guild){
		return AudioUtil.isAudioConnectionOpen(guild);
	}
}