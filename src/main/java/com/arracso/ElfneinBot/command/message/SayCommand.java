package com.arracso.ElfneinBot.command.message;

import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;

public class SayCommand extends MessageCommand {

	public SayCommand(){
		commandName = "say";
	}
	
	@Override
	public Mono<Void> execute(Message message) {
		System.out.println(message.getContent());
		return message.getChannel().block()
			.createMessage(message.getContent().replaceFirst(".*?say", "").trim())
			.then();
	}
}
