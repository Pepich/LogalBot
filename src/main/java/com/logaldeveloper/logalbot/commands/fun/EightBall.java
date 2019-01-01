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

package com.logaldeveloper.logalbot.commands.fun;

import com.logaldeveloper.logalbot.commands.Command;
import com.logaldeveloper.logalbot.commands.CommandResponse;
import com.logaldeveloper.logalbot.utils.StringUtil;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public final class EightBall implements Command {
	private static final ArrayList<String> responses = new ArrayList<>();
	private static final Random rng = new Random();

	@Override
	public void initialize(){
		responses.add("It is certain");
		responses.add("It is decidedly so");
		responses.add("Without a doubt");
		responses.add("Yes - definitely");
		responses.add("You may rely on it");
		responses.add("As I see it, yes");
		responses.add("Most likely");
		responses.add("Outlook good");
		responses.add("Yes");
		responses.add("Signs point to yes");

		responses.add("Reply hazy, try again");
		responses.add("Ask again later");
		responses.add("Better not tell you now");
		responses.add("Cannot predict now");
		responses.add("Concentrate and ask again");

		responses.add("Don't count on it");
		responses.add("My reply is no");
		responses.add("My sources say no");
		responses.add("Outlook not so good");
		responses.add("Very doubtful");
	}

	@Override
	public CommandResponse execute(String[] arguments, User executor, TextChannel channel){
		if (arguments.length == 0){
			return new CommandResponse("no_entry_sign", "Sorry, " + executor.getAsMention() + ", but you need to supply a question for the Magic 8 Ball.").setDeletionDelay(10, TimeUnit.SECONDS);
		}

		String question = StringUtil.sanatizeCodeBlock(String.join(" ", arguments));

		return new CommandResponse("question", executor.getAsMention() + " asked the Magic 8 Ball: `" + question + "`\n:8ball: The Magic 8 Ball responds: *" + responses.get(rng.nextInt(responses.size())) + "*.");
	}
}