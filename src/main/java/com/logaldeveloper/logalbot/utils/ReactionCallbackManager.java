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

import com.logaldeveloper.logalbot.commands.ReactionCallback;
import net.dv8tion.jda.core.entities.Member;

import java.util.HashMap;

public final class ReactionCallbackManager {
	private static final HashMap<String, HashMap<String, ReactionCallback>> callbackDictionary = new HashMap<>();
	private static final HashMap<String, String> targetDictionary = new HashMap<>();

	public static void registerCallback(String messageID, String emoji, ReactionCallback callback){
		if (!callbackDictionary.containsKey(messageID)){
			callbackDictionary.put(messageID, new HashMap<>());
		}

		callbackDictionary.get(messageID).put(emoji, callback);
	}

	public static void setCallbackTarget(Member member, String messageID){
		targetDictionary.put(messageID, member.getUser().getId());
	}

	public static void unregisterMessage(String messageID){
		callbackDictionary.remove(messageID);
		targetDictionary.remove(messageID);
	}

	public static void executeCallback(String messageID, Member reactor, String emoji){
		if (callbackDictionary.containsKey(messageID)){
			if (targetDictionary.containsKey(messageID) && !targetDictionary.get(messageID).equals(reactor.getUser().getId())){
				return;
			}

			if (callbackDictionary.get(messageID).containsKey(emoji)){
				callbackDictionary.get(messageID).get(emoji).run(reactor, messageID);
			}
		}
	}
}