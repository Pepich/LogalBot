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

package com.logaldeveloper.logalbot.events;

import com.logaldeveloper.logalbot.utils.AudioUtil;
import com.logaldeveloper.logalbot.utils.DataManager;
import com.logaldeveloper.logalbot.utils.StringUtil;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.SelfUser;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public final class GuildJoin extends ListenerAdapter {
	@Override
	public void onGuildJoin(GuildJoinEvent event){
		Guild guild = event.getGuild();
		if (!AudioUtil.isInitialized(guild)){
			AudioUtil.initialize(guild);
		}

		if (DataManager.getGuildValue(guild, "known") == null){
			DataManager.setGuildValue(guild, "known", "true");

			SelfUser self = event.getJDA().getSelfUser();
			MessageBuilder builder = new MessageBuilder();
			builder.append(":wave: Hello there members of ").append(StringUtil.sanatize(guild.getName())).append("! I'm ").append(self.getName()).append(", and I'm glad to be here to serve all of you.");
			builder.append("\n\nYou can tell me what to do by prefixing a message with a direct mention to me followed by a command word and any arguments for it. Here's an example:");
			builder.append("\n`@").append(self.getName()).append("#").append(self.getDiscriminator()).append(" 8ball Is it rewind time?`");
			builder.append("\nYou can find a list of commands I know about here: https://logaldeveloper.com/projects/logalbot/command-reference/");
			builder.append("\n\nBy default, I only allow ").append(event.getGuild().getOwner().getAsMention()).append(" to run important commands, but you can ask them to add you to my whitelist where I will also allow you to run those commands.");

			for (TextChannel channel : guild.getTextChannels()){
				if (guild.getSelfMember().hasPermission(channel, Permission.MESSAGE_WRITE)){
					channel.sendMessage(builder.build()).queue();
					break;
				}
			}
		}
	}
}