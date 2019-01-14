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

public final class StringUtil {
	public static String sanatize(String string){
		return string.replaceAll("([_*`<@>~])", "\\\\$1").replaceAll("[\r\n]", "");
	}

	public static String sanatizeCodeBlock(String string){
		return string.replaceAll("[`]", "'").replaceAll("[\r\n]", "");
	}

	public static String formatTime(long milliseconds){
		long second = (milliseconds / 1000) % 60;
		long minute = (milliseconds / (1000 * 60)) % 60;
		long hour = (milliseconds / (1000 * 60 * 60)) % 24;

		return String.format("%02d:%02d:%02d", hour, minute, second);
	}

	public static String intToUnicodeEmoji(int number){
		switch (number){
			case 0:
				return "0⃣";
			case 1:
				return "1⃣";
			case 2:
				return "2⃣";
			case 3:
				return "3⃣";
			case 4:
				return "4⃣";
			case 5:
				return "5⃣";
			case 6:
				return "6⃣";
			case 7:
				return "7⃣";
			case 8:
				return "8⃣";
			case 9:
				return "9⃣";
			case 10:
				return "\uD83D\uDD1F";
			default:
				return "";
		}
	}

	public static String intToEmoji(int number){
		switch (number){
			case 0:
				return ":zero:";
			case 1:
				return ":one:";
			case 2:
				return ":two:";
			case 3:
				return ":three:";
			case 4:
				return ":four:";
			case 5:
				return ":five:";
			case 6:
				return ":six:";
			case 7:
				return ":seven:";
			case 8:
				return ":eight:";
			case 9:
				return ":nine:";
			case 10:
				return ":keycap_ten:";
			default:
				return "";
		}
	}
}