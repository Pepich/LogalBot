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

public class EmojiUtil {
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
				return null;
		}
	}
}