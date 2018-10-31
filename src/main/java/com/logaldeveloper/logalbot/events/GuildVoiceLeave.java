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

import com.logaldeveloper.logalbot.Main;
import com.logaldeveloper.logalbot.audio.TrackScheduler;
import com.logaldeveloper.logalbot.utils.AudioUtil;
import com.logaldeveloper.logalbot.utils.VoiceChannelUtil;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class GuildVoiceLeave extends ListenerAdapter {
	private static final Logger logger = LoggerFactory.getLogger(GuildVoiceLeave.class);

	@Override
	public void onGuildVoiceLeave(GuildVoiceLeaveEvent event){
		if (!event.getGuild().equals(Main.getGuild())){
			return;
		}

		if (!VoiceChannelUtil.isConnectedToVoiceChannel()){
			return;
		}

		if (!AudioUtil.isTrackLoaded()){
			return;
		}

		Member member = event.getMember();

		if (member.getUser().equals(Main.getJDA().getSelfUser())){
			return;
		}

		VoiceChannel leftChannel = event.getChannelLeft();

		if (leftChannel.equals(VoiceChannelUtil.getCurrentVoiceChannel())){
			List<Member> members = leftChannel.getMembers();
			if (members.size() == 1 && members.get(0).getUser().equals(Main.getJDA().getSelfUser())){
				logger.info("All listeners left the voice channel.");
				TrackScheduler.clearQueue();
				if (AudioUtil.isTrackLoaded()){
					AudioUtil.stopTrack();
				}
			}
		}
	}
}