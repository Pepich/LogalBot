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

package com.logaldeveloper.logalbot.commands.general;

import com.logaldeveloper.logalbot.Main;
import com.logaldeveloper.logalbot.commands.Command;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

public class About implements Command {
	@Override
	public void initialize(){
	}

	@Override
	public String execute(String[] arguments, User executor, TextChannel channel){
		String message = "";
		message += ":wave: Hey there " + executor.getAsMention() + "! ";
		if (Main.getJDA().getSelfUser().getName().equalsIgnoreCase("LogalBot")){
			message += "I'm " + Main.getJDA().getSelfUser().getName() + ", a bot created by LogalDeveloper but maintained by " + Main.getOwner().getAsMention() + ".\n";
			message += "My GitHub repository can be found here: https://github.com/LogalDeveloper/LogalBot";
		} else {
			message += "I'm " + Main.getJDA().getSelfUser().getName() + ", a fork of LogalBot maintained by " + Main.getOwner().getAsMention() + ".\n";
			message += "My core GitHub repository can be found here: https://github.com/LogalDeveloper/LogalBot";
		}
		return message;
	}
}