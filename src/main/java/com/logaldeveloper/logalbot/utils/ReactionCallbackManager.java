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
import net.dv8tion.jda.core.entities.User;

import java.util.HashMap;

public final class ReactionCallbackManager {
	private static HashMap<String, HashMap<String, ReactionCallback>> callbackDictonary = new HashMap<>();
	private static HashMap<String, String> targetDictonary = new HashMap<>();

	public static void registerCallback(String messageID, String emoji, ReactionCallback callback){
		if (!callbackDictonary.containsKey(messageID)){
			callbackDictonary.put(messageID, new HashMap<>());
		}

		callbackDictonary.get(messageID).put(emoji, callback);
	}

	public static void setCallbackTarget(User user, String messageID){
		targetDictonary.put(messageID, user.getId());
	}

	public static void unregisterMessage(String messageID){
		callbackDictonary.remove(messageID);
		targetDictonary.remove(messageID);
	}

	public static void executeCallback(String messageID, User reactor, String emoji){
		if (callbackDictonary.containsKey(messageID)){
			if (targetDictonary.containsKey(messageID) && !targetDictonary.get(messageID).equals(reactor.getId())){
				return;
			}

			if (callbackDictonary.get(messageID).containsKey(emoji)){
				callbackDictonary.get(messageID).get(emoji).run(reactor, messageID);
			}
		}
	}
}