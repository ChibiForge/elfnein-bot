package com.arracso.ElfneinBot.command.message;

import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;

public class PingCommand extends MessageCommand {

	public PingCommand(){
		commandName = "ping";
	}
	
	@Override
	public Mono<Void> execute(Message message) {
		return message.getChannel().block()
			.createMessage("Pong!")
			.withMessageReference(message.getId())
		    .then();
	}
}
