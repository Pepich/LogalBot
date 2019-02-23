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

import com.logaldeveloper.logalbot.commands.CommandManager;
import com.logaldeveloper.logalbot.utils.DataManager;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.SelfUser;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.Arrays;
import java.util.List;

public final class GuildMessageReceived extends ListenerAdapter {
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event){
		Member self = event.getGuild().getSelfMember();
		TextChannel channel = event.getChannel();
		if (event.getAuthor().isBot() || event.getMessage().isTTS() || !self.hasPermission(channel, Permission.MESSAGE_WRITE)){
			return;
		}

		Message message = event.getMessage();
		String content = message.getContentRaw();
		SelfUser selfUser = event.getJDA().getSelfUser();
		List<Member> mentionedMembers = message.getMentionedMembers();
		if (mentionedMembers.size() >= 1 && mentionedMembers.get(0).getUser().getId().equals(selfUser.getId()) && (content.startsWith(self.getAsMention()) || content.startsWith(selfUser.getAsMention()))){
			String[] rawCommand = content.split(" ");
			String[] command = Arrays.copyOfRange(rawCommand, 1, rawCommand.length);
			if (command.length >= 1){
				if (self.hasPermission(channel, Permission.MESSAGE_MANAGE)){
					message.delete().reason("LogalBot Command Execution").queue();
				}
				CommandManager.executeCommand(command, event.getMember(), channel);
			}
		} else {
			String commandCharacter = DataManager.getGuildValue(event.getGuild(), "commandCharacter");
			if (commandCharacter == null){
				return;
			}

			char commandChar = commandCharacter.toCharArray()[0];

			if (content.length() > 1 && content.charAt(0) == commandChar){
				String[] command = content.substring(1).split(" ");
				if (self.hasPermission(channel, Permission.MESSAGE_MANAGE)){
					message.delete().reason("LogalBot Command Execution").queue();
				}
				CommandManager.executeCommand(command, event.getMember(), channel);
			}
		}
	}
}