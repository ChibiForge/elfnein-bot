package com.chibiforge.elfnein.command.message;

import com.chibiforge.elfnein.util.Service;
import com.chibiforge.elfnein.util.Util;

import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;

public class RankFixCommand extends MessageCommand {

	public RankFixCommand(){
		commandNames.add("rankfix");
		commandNames.add("fixrank");
		commandId = 3;
	}
	
	@Override
	public Mono<Void> execute(Message message) {
		if(!message.getAuthor().isPresent())
			return Util.replyToMessage(message, "Something went wrong! Cannot retrieve user id. Please tell <@278957461120090113> to fix me!").then();
		
		String userId = message.getAuthor().get().getId().asString();
		
		if(!userId.equals("278957461120090113"))
			return Util.replyToMessage(message, "This command can only be used by <@278957461120090113>!").then();
		
		if(!message.getGuildId().isPresent())
			return Util.replyToMessage(message, "Something went wrong! Cannot retrieve guild id. Please tell <@278957461120090113> to fix me!").then();
		
		String serverId = message.getGuildId().get().asString();
		
		Service.userService.fixServerActivity(serverId);
		
		return Util.replyToMessage(message, "Fixed server activity levels.").then();
	}
	
}
