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

package com.logaldeveloper.logalbot.events;

import com.logaldeveloper.logalbot.Main;
import com.logaldeveloper.logalbot.commands.CommandManager;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.SelfUser;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.Arrays;
import java.util.List;

public class GuildMessageReceived extends ListenerAdapter {
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event){
		if (!event.getGuild().equals(Main.getGuild())){
			return;
		}

		if (event.getAuthor().isBot() || event.getMessage().isTTS()){
			return;
		}

		Message message = event.getMessage();
		String rawMessage = message.getContentRaw();
		SelfUser selfUser = event.getJDA().getSelfUser();
		List<Member> mentionedMembers = message.getMentionedMembers();
		if (mentionedMembers.size() >= 1 && mentionedMembers.get(0).getUser().getId().equals(selfUser.getId()) && rawMessage.startsWith(selfUser.getAsMention())){
			String[] rawCommand = rawMessage.split(" ");
			String[] command = Arrays.copyOfRange(rawCommand, 1, rawCommand.length);
			if (command.length >= 1){
				CommandManager.executeCommand(command, event.getAuthor(), event.getChannel(), message);
			}
		}
	}
}