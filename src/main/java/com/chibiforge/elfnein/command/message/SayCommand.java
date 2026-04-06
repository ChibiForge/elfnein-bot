package com.chibiforge.elfnein.command.message;

import com.chibiforge.elfnein.util.Global;

import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;

public class SayCommand extends MessageCommand {

	public SayCommand(){
		commandNames.add("say");
		commandId = Global.cmdIdSay;
	}
	
	@Override
	public Mono<Void> execute(Message message) {
		String text = stripCommand(message);
		if(text.contains("@")) {
			if(!message.getAuthor().get().getId().asString().equals("278957461120090113")) {
				text = text.replace("<@", "<@!");
			}
		}
		return message.getChannel().block()
			.createMessage(text)
			.then(message.delete().then()).then();
	}
}
