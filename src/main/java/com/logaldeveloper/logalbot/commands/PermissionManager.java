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

package com.logaldeveloper.logalbot.commands;

import com.logaldeveloper.logalbot.utils.DataManager;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PermissionManager {
	private static final Logger logger = LoggerFactory.getLogger(PermissionManager.class);

	public static boolean isWhitelisted(Member member){
		if (member.hasPermission(Permission.ADMINISTRATOR)){
			return true;
		}

		if (DataManager.getUserValue(member, "whitelisted") == null){
			DataManager.setUserValue(member, "whitelisted", "false");
		}

		return DataManager.getUserValue(member, "whitelisted").equals("true");
	}

	public static void addToWhitelist(Member member){
		DataManager.setUserValue(member, "whitelisted", "true");
		logger.info(member.getEffectiveName() + " (" + member.getUser().getId() + ") was added to the whitelist in " + member.getGuild().getName() + " (" + member.getGuild().getId() + ").");
	}

	public static void removeFromWhitelist(Member member){
		DataManager.setUserValue(member, "whitelisted", "false");
		logger.info(member.getEffectiveName() + " (" + member.getUser().getId() + ") was removed from the whitelist in " + member.getGuild().getName() + " (" + member.getGuild().getId() + ").");
	}
}