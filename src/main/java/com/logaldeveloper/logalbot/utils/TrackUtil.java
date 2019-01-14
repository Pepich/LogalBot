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

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;

import java.util.ArrayList;

public final class TrackUtil {
	public static MessageEmbed generateTrackInfoEmbed(AudioTrack track){
		EmbedBuilder builder = new EmbedBuilder();
		builder.addField(StringUtil.sanatize(track.getInfo().title), StringUtil.sanatize(track.getInfo().author) + " - " + StringUtil.formatTime(track.getDuration()), false);
		return builder.build();
	}

	public static MessageEmbed generateTrackListInfoEmbed(ArrayList<AudioTrack> tracks, boolean numbered){
		EmbedBuilder builder = new EmbedBuilder();
		int i = 1;
		for (AudioTrack track : tracks){
			if (numbered){
				builder.addField(StringUtil.intToEmoji(i) + " " + StringUtil.sanatize(track.getInfo().title), StringUtil.sanatize(track.getInfo().author) + " - " + StringUtil.formatTime(track.getDuration()), false);
				i++;
			} else {
				builder.addField(StringUtil.sanatize(track.getInfo().title), StringUtil.sanatize(track.getInfo().author) + " - " + StringUtil.formatTime(track.getDuration()), false);
			}

		}
		return builder.build();
	}
}