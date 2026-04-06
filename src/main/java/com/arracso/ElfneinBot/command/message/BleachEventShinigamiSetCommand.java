package com.arracso.ElfneinBot.command.message;

import java.util.List;
import java.util.stream.Collectors;

import com.arracso.ElfneinBot.util.Global;
import com.arracso.ElfneinBot.util.Service;
import com.arracso.ElfneinBot.util.Util;

import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;

public class BleachEventShinigamiSetCommand extends MessageCommand {
	
	public BleachEventShinigamiSetCommand(){
		commandNames.add("shinigami set");
		commandNames.add("shinigami add");
		commandId = Global.bleachEventBase;
	}
	
	@Override
	public Mono<Void> execute(Message message) {
		if(!message.getAuthor().isPresent())
			return Util.replyToMessage(message,"Something went wrong! Cannot retrieve user id. Please tell <@" + Service.properties.getDeveloperID() + "> to fix me!").then();
		
		if(!message.getGuildId().isPresent())
			return Util.replyToMessage(message,"Something went wrong! Cannot retrieve guild id. Please tell <@" + Service.properties.getDeveloperID() + "> to fix me!").then();		
		
		return message.getAuthorAsMember().flatMap(member -> {
			if(!List.of("696347459751903293","278957461120090113","1237588701702520905").contains(member.getId().asString()))
				return Util.replyToMessage(message,"Nice try! But you cannot use that!").then();		
			
			String type = message.getContent().startsWith("shinigami set")? "set" : "add";
			
			List<String> parameters = getParameters(message);
			if(parameters.size()<=2)
				return Util.replyToMessage(message,"Missing arguments: [user] [stat] [value]").then();	
			
			String targetId = parameters.get(0).startsWith("<@")?parameters.get(0).split("@")[1].split(">")[0]: parameters.get(0);
			String stat = parameters.get(1);
			String value = parameters.stream().skip(2).collect(Collectors.joining(" "));
			
			if(!Service.bleachEventService.isUserInEvent(member.getGuildId().asString(),targetId))
				return Util.replyToMessage(message,"Cannot find this Shinigami.").then();
			
			return member.getGuild().flatMap(guild -> 
				Util.replyToMessage(message, Service.bleachEventService.updateUser(type,guild.getId().asString(),targetId,stat,value)).then()
			);
		});
	}	
}
