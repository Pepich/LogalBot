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

import java.util.ArrayList;
import java.util.HashMap;

public final class SkipManager {
	private static final HashMap<String, ArrayList<String>> skipVotesDictionary = new HashMap<>();

	public static void registerVote(Member member){
		if (!skipVotesDictionary.containsKey(member.getGuild().getId())){
			resetVotes(member.getGuild());
		}

		ArrayList<String> registeredVotes = skipVotesDictionary.get(member.getGuild().getId());
		if (!registeredVotes.contains(member.getUser().getId())){
			registeredVotes.add(member.getUser().getId());
		}
	}

	public static boolean hasVoted(Member member){
		return skipVotesDictionary.get(member.getGuild().getId()).contains(member.getUser().getId());
	}

	public static void resetVotes(Guild guild){
		if (skipVotesDictionary.containsKey(guild.getId())){
			skipVotesDictionary.get(guild.getId()).clear();
		} else {
			skipVotesDictionary.put(guild.getId(), new ArrayList<>());
		}
	}

	public static boolean shouldSkip(Guild guild){
		int listeners = (int) VoiceChannelUtil.getCurrentVoiceChannel(guild).getMembers().stream().filter(member -> !member.getUser().isBot()).count();
		int required = (int) Math.ceil(listeners * .55);

		return (skipVotesDictionary.get(guild.getId()).size() >= required);
	}

	public static int getRemainingRequired(Guild guild){
		int listeners = (int) VoiceChannelUtil.getCurrentVoiceChannel(guild).getMembers().stream().filter(member -> !member.getUser().isBot()).count();
		int required = (int) Math.ceil(listeners * .55);

		return (required - skipVotesDictionary.get(guild.getId()).size());
	}
}