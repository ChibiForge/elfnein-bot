package com.chibiforge.elfnein.command.message;

import com.chibiforge.elfnein.util.Global;

import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;

public class RepeatCommand extends MessageCommand {

	public RepeatCommand(){
		commandId = Global.cmdIdRepeate;
	}
	
	@Override
	public Boolean check(Message message) {
		return true;
	}
	
	@Override
	public Mono<Void> execute(Message message) {
		return message.getAuthorAsMember().flatMap(member ->{
			String text = "__**" + member.getDisplayName() + "**__: " + message.getContent();
			return message.getChannel().block()
				.createMessage(text)
				//.then(message.delete().then())
				.then();
		});
	}
}
