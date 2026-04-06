package com.arracso.ElfneinBot.command.message;

import discord4j.core.object.entity.Message;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;
import reactor.core.publisher.Mono;

public class HelpCommand extends MessageCommand {

	public HelpCommand(){
		commandNames.add("help");
		commandNames.add("h");
		commandId = 0;
	}
	
	@Override
	public Mono<Void> execute(Message message) {
		EmbedCreateSpec.Builder embedBuilder = EmbedCreateSpec.builder();
		embedBuilder.color(Color.SEA_GREEN);
        embedBuilder.title("Help page");
        embedBuilder.description("This bot has been created by <@278957461120090113> please go to the [support server](https://discord.gg/2Ke6E4jQrg) for any questions related to the bot.\nHere are some of the features the bot has:");
        embedBuilder.addField("Date Solver","You can ask on the support server how to set it up.\n You might also use the following commands:\n- solve: to solve a date\n- map: to check the readed map",false);
        embedBuilder.footer("Version 1.0.1", null);
        
		return message.getChannel().block()
			.createMessage(embedBuilder.build())
			.withMessageReference(message.getData().messageReference())
			.then();
	}
}
