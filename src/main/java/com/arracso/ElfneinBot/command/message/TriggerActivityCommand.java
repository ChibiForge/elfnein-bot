package com.arracso.ElfneinBot.command.message;


import com.arracso.ElfneinBot.util.Service;
import com.arracso.ElfneinBot.util.Util;

import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;

public class TriggerActivityCommand extends MessageCommand {
	
	public TriggerActivityCommand(){
		this.commandId = 3;
	}

	
	@Override
	public Boolean check(Message message) {
		if(message.getAuthor().map(user -> !user.isBot()).orElse(false)) return true;
		return false;
	}
	
	@Override
	public Mono<Void> execute(Message message) {
		
		if(!message.getAuthor().isPresent()) return Util.replyToMessage(message, "Something went wrong! Cannot retrieve user id. Please tell <@278957461120090113> to fix me!").then();
		
		String userId = message.getAuthor().get().getId().asString();
		
		if(!message.getGuildId().isPresent()) return Util.replyToMessage(message, "Something went wrong! Cannot retrieve guild id. Please tell <@278957461120090113> to fix me!").then();
		
		String serverId = message.getGuildId().get().asString();
		
		if(Service.userService.updateUserActivity(serverId,userId)) {
			// TODO - Level up
		}
		
		
		return Mono.empty();
	}

}
