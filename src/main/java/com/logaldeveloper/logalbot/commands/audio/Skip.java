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

package com.logaldeveloper.logalbot.commands.audio;

import com.logaldeveloper.logalbot.Main;
import com.logaldeveloper.logalbot.commands.Command;
import com.logaldeveloper.logalbot.commands.CommandResponse;
import com.logaldeveloper.logalbot.utils.AudioUtil;
import com.logaldeveloper.logalbot.utils.SkipManager;
import com.logaldeveloper.logalbot.utils.TrackUtil;
import com.logaldeveloper.logalbot.utils.VoiceChannelUtil;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

import java.util.concurrent.TimeUnit;

public final class Skip implements Command {
	@Override
	public CommandResponse execute(String[] arguments, User executor, TextChannel channel){
		if (!AudioUtil.isAllowedChannelForAudioCommands(channel)){
			return new CommandResponse("no_entry_sign", "Sorry " + executor.getAsMention() + ", but audio commands can only be used in text channels named `" + Main.getTextChannelNameForAudioCommands() + "`.").setDeletionDelay(10, TimeUnit.SECONDS);
		}

		if (!AudioUtil.isTrackLoaded(channel.getGuild())){
			return new CommandResponse("no_entry_sign", "Sorry " + executor.getAsMention() + ", but there must be a track playing in order to vote to skip it.").setDeletionDelay(10, TimeUnit.SECONDS);
		}

		if (!VoiceChannelUtil.isInCurrentVoiceChannel(channel.getGuild(), executor)){
			return new CommandResponse("no_entry_sign", "Sorry " + executor.getAsMention() + ", but you must be in voice channel `" + AudioUtil.getCurrentVoiceChannel(channel.getGuild()).getName() + "` in order to vote to skip tracks.").setDeletionDelay(10, TimeUnit.SECONDS);
		}

		Guild guild = channel.getGuild();
		if (SkipManager.hasVoted(guild, executor)){
			return new CommandResponse("no_entry_sign", "You have already voted to skip this track " + executor.getAsMention() + ".").setDeletionDelay(10, TimeUnit.SECONDS);
		}

		SkipManager.registerVote(guild, executor);
		if (SkipManager.shouldSkip(guild)){
			AudioTrack skippedTrack = AudioUtil.getLoadedTrack(guild);
			AudioUtil.getTrackScheduler(guild).skipCurrentTrack();
			CommandResponse response = new CommandResponse("gun", "The following track has been skipped:");
			response.attachEmbed(TrackUtil.generateTrackInfoEmbed(skippedTrack));
			return response;
		} else {
			if (SkipManager.getRemainingRequired(guild) == 1){
				return new CommandResponse("x", executor.getAsMention() + " has voted to skip this track. " + SkipManager.getRemainingRequired(guild) + " more vote is needed.");
			} else {
				return new CommandResponse("x", executor.getAsMention() + " has voted to skip this track. " + SkipManager.getRemainingRequired(guild) + " more votes are needed.");
			}
		}
	}
}