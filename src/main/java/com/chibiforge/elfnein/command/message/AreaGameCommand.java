package com.chibiforge.elfnein.command.message;


import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;

import com.chibiforge.elfnein.util.Global;
import com.chibiforge.elfnein.util.Util;

import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.object.component.Container;
import discord4j.core.object.component.Separator;
import discord4j.core.object.component.TextDisplay;
import discord4j.core.object.entity.Message;
import discord4j.rest.util.Color;
import reactor.core.publisher.Mono;

public class AreaGameCommand extends MessageCommand {
	
	@Value("${discord.bot.developer.id}")
	private String developerID;
	
	public AreaGameCommand(){
		commandNames.add("area");
		commandId = Global.cmdIdArea;
	}
	
	@Override
	public Mono<Void> execute(Message message) {
		// Get author
		if(!message.getAuthor().isPresent())
			return Util.replyToMessage(message, "Something went wrong! Cannot retrieve user id. Please tell <@" + developerID + "> to fix me!").then();
		String player1 = message.getAuthor().get().getId().asString();
		
		// Get opponent
		List<String> parameter = getParameters(message);
		if(parameter.isEmpty()) return Util.replyToMessage(message, "You need to specify an opponent!").then();
		String player2 = parameter.get(0); // TODO check
		if(parameter.get(0).startsWith("<@")) player2 = parameter.get(0).split("@")[1].split(">")[0];
		
		// Check the 2 players are diff
		//if(player1.equals(player2)) return Util.replyToMessage(message, "You cannot play against yourselt silly.").then();
		
		// Set buttons
		List<Button> buttons = new ArrayList<Button>();
		buttons.add(Button.secondary("area:start:accept:p1=" + player1 + "&p2=" + player2, "Accept"));
		buttons.add(Button.secondary("area:start:decline:p1=" + player1 + "&p2=" + player2, "Decline"));
		
		Container body = Container.of(
				Color.SEA_GREEN,
				TextDisplay.of("Someone is challenging you!"),
				Separator.of(),
				TextDisplay.of("**Game:** Area"),
				TextDisplay.of("**Mode:** Classic"),
				TextDisplay.of("**Player 1:** <@" + player1 + ">"),
				TextDisplay.of("**Player 2:** <@" + player2 + ">"),
				Separator.of(),
				TextDisplay.of("Will you accept the challenge?")
			);
		
		// Send request
		return message.getChannel().flatMap(channel -> channel.createMessage()
				.withFlags(Message.Flag.IS_COMPONENTS_V2)
				.withComponents(body,ActionRow.of(buttons))
			).then();
	}
	
}
