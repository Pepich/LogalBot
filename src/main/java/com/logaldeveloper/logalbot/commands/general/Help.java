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

package com.logaldeveloper.logalbot.commands.general;

import com.logaldeveloper.logalbot.commands.Command;
import com.logaldeveloper.logalbot.commands.CommandResponse;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

public final class Help implements Command {
	@Override
	public CommandResponse execute(String[] arguments, User executor, TextChannel channel){
		EmbedBuilder builder = new EmbedBuilder();
		builder.addField(":desktop: Repository", "https://github.com/LogalDeveloper/LogalBot", false);
		builder.addField(":page_facing_up: Command Reference", "https://logaldeveloper.com/projects/logalbot/command-reference/", false);
		builder.addField(":bug: Issue Tracker", "https://github.com/LogalDeveloper/LogalBot/issues", false);

		CommandResponse response = new CommandResponse("link", executor.getAsMention() + ", here are some helpful links:");
		response.attachEmbed(builder.build());
		return response;
	}
}