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

import java.util.List;

public final class TrackUtil {
	public static MessageEmbed generateTrackInfoEmbed(AudioTrack track){
		EmbedBuilder builder = new EmbedBuilder();
		builder.addField(StringUtil.sanitize(track.getInfo().title), StringUtil.sanitize(track.getInfo().author) + " - " + StringUtil.formatTime(track.getDuration()), false);
		return builder.build();
	}

	public static MessageEmbed generateTrackListInfoEmbed(List<AudioTrack> tracks, boolean numbered){
		EmbedBuilder builder = new EmbedBuilder();

		for (int i = 0; i < tracks.size(); i++){
			if (i == 10){
				break;
			}

			AudioTrack track = tracks.get(i);
			if (numbered){
				builder.addField("**" + (i + 1) + ":** " + StringUtil.sanitize(track.getInfo().title), StringUtil.sanitize(track.getInfo().author) + " - " + StringUtil.formatTime(track.getDuration()), false);
			} else {
				builder.addField(StringUtil.sanitize(track.getInfo().title), StringUtil.sanitize(track.getInfo().author) + " - " + StringUtil.formatTime(track.getDuration()), false);
			}
		}

		if (tracks.size() > 10){
			builder.setTitle("**Top 10 Tracks - " + (tracks.size() - 10) + " Not Shown**");
		}
		return builder.build();
	}

	public static MessageEmbed generatePaginatedTrackListInfoEmbed(List<AudioTrack> tracks, int page){
		EmbedBuilder builder = new EmbedBuilder();

		if (page < 1){
			page = 1;
		}

		int pages = (int) Math.ceil(tracks.size() / 10d);

		if (page > pages){
			page = pages;
		}

		page = page - 1;
		int start = page * 10;
		int end = start + 10;

		for (int i = start; i < end && i < tracks.size(); i++){
			AudioTrack track = tracks.get(i);
			builder.addField("**" + (i + 1) + ":** " + StringUtil.sanitize(track.getInfo().title), StringUtil.sanitize(track.getInfo().author) + " - " + StringUtil.formatTime(track.getDuration()), false);
		}

		builder.setTitle("**" + tracks.size() + " Total Tracks - Page " + (page + 1) + "/" + pages + "**");
		return builder.build();
	}
}