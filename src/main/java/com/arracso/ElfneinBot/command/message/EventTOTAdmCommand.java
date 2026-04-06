package com.arracso.ElfneinBot.command.message;

import java.util.List;

import com.arracso.ElfneinBot.util.Global;
import com.arracso.ElfneinBot.util.Service;
import com.arracso.ElfneinBot.util.Util;

import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;

public class EventTOTAdmCommand extends MessageCommand {
	
	public EventTOTAdmCommand(){
		commandNames.add("deltot");
		commandNames.add("swaptot");
		commandId = Global.eventTOTAdmin;
	}
	
	@Override
	public Mono<Void> execute(Message message) {
		if(!message.getAuthor().isPresent())
			return Util.replyToMessage(message,"Something went wrong! Cannot retrieve user id. Please tell <@" + Service.properties.getDeveloperID() + "> to fix me!").then();
		
		if(!message.getGuildId().isPresent())
			return Util.replyToMessage(message,"Something went wrong! Cannot retrieve guild id. Please tell <@" + Service.properties.getDeveloperID() + "> to fix me!").then();		
		
		return message.getAuthorAsMember().flatMap(member -> {			
			List<String> parameter = getParameters(message);
			String cmdName = getCommandName(message);
			if(cmdName.equals("swaptot")) {
				Service.eventTOTService.swapHouseLocations(member.getGuildId().asString());
				return Util.replyToMessage(message,"Houses shuffled.").then();
			} else if(parameter.size()>1 && parameter.get(0).toLowerCase().equals("user")) {
				String userId = parseId(parameter.get(1));
				if(Service.eventTOTService.deleteUser(member.getGuildId().asString(), userId)) return Util.replyToMessage(message,"User deleted.").then();
				return Util.replyToMessage(message,"User not on the database.").then();
			} else if (parameter.size()>1 && parameter.get(0).toLowerCase().equals("house")) {
				String userId = parseId(parameter.get(1));
				if(Service.eventTOTService.deleteHouse(member.getGuildId().asString(), userId)) return Util.replyToMessage(message,"User house deleted.").then();
				return Util.replyToMessage(message,"User not on the database.").then();
			}
			return Util.replyToMessage(message,"Usage: `e.deltot user [user]` or `e.deltot house [user]`").then();
		});
	}
	
	private String parseId(String par) {
		String id = par;
		if(par.startsWith("<@")) id = par.split("@")[1].split(">")[0];
		return id;
	}
	
}
