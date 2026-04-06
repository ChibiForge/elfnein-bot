package com.arracso.ElfneinBot.command.message;


import java.util.List;

import com.arracso.ElfneinBot.util.Global;
import com.arracso.ElfneinBot.util.Service;
import com.arracso.ElfneinBot.util.Util;

import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;

public class KarutaLoopSizeCommand extends MessageCommand {
	
	public KarutaLoopSizeCommand(){
		commandNames.add("loopsize");
		commandNames.add("setloop");
		commandNames.add("setloopsize");
		commandNames.add("setsize");
		commandId = Global.cmdIdLoop;
	}
	
	@Override
	public Mono<Void> execute(Message message) {
		// Get parameters
		List<String> parameters = getParameters(message);
		if(parameters.isEmpty())
			return Util.replyToMessage(message, "*Command usage:* `loopsize size`\nUse this command to setup the default size of the loop when runing the loop command.").then();
		
		// Get loop size
		Integer loopSize = Integer.valueOf(parameters.get(0));
		
		// Get author
		if(!message.getAuthor().isPresent())
			return Util.replyToMessage(message, "Something went wrong! Cannot retrieve user id. Please tell <@278957461120090113> to fix me!").then();
		
		String userId = message.getAuthor().get().getId().asString();
		
		Service.userService.setUserLoopSize(userId, loopSize);
		
		return Util.replyToMessage(message, "Set your default loop size to " + loopSize).then();
	}
	
}
