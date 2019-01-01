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

package com.logaldeveloper.logalbot.events;

import com.logaldeveloper.logalbot.utils.AudioUtil;
import com.logaldeveloper.logalbot.utils.VoiceChannelUtil;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class GuildVoiceMove extends ListenerAdapter {
	private static final Logger logger = LoggerFactory.getLogger(GuildVoiceMove.class);

	@Override
	public void onGuildVoiceMove(GuildVoiceMoveEvent event){
		if (!VoiceChannelUtil.isConnectedToVoiceChannel(event.getGuild())){
			return;
		}

		if (!AudioUtil.isTrackLoaded(event.getGuild())){
			return;
		}

		Member member = event.getMember();

		if (member.getUser().equals(event.getJDA().getSelfUser())){
			return;
		}

		VoiceChannel leftChannel = event.getChannelLeft();

		if (leftChannel.equals(VoiceChannelUtil.getCurrentVoiceChannel(event.getGuild()))){
			List<Member> members = leftChannel.getMembers();
			if (members.size() == 1 && members.get(0).getUser().equals(event.getJDA().getSelfUser())){
				logger.info("All listeners left the voice channel.");
				AudioUtil.getTrackScheduler(event.getGuild()).clearQueue();
				if (AudioUtil.isTrackLoaded(event.getGuild())){
					AudioUtil.stopTrack(event.getGuild());
				}
			}
		}
	}
}