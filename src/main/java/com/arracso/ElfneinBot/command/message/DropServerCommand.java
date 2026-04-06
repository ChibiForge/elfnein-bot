package com.arracso.ElfneinBot.command.message;

import java.util.List;

import com.arracso.ElfneinBot.util.Global;
import com.arracso.ElfneinBot.util.Service;
import com.arracso.ElfneinBot.util.Util;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Message;
import discord4j.rest.http.client.ClientException;
import reactor.core.publisher.Mono;

public class DropServerCommand extends MessageCommand {
	
	public DropServerCommand(){
		commandNames.add("dropserver");
		commandNames.add("ds");
		commandId = Global.cmdAdmin;
	}
	
	@Override
	public Mono<Void> execute(Message message) {
		if(!message.getAuthor().isPresent())
			return Util.replyToMessage(message,"Something went wrong! Cannot retrieve user id. Please tell <@" + Service.properties.getDeveloperID() + "> to fix me!").then();
		
		// Get command parameters
		List<String> commandParameters = getParameters(message);
		if(commandParameters.isEmpty())
			return Util.replyToMessage(message, "*Command usage:* `dropserver serverID`").then();
		
		String guildID = commandParameters.get(0);
		
		// Validate the guild ID format
        if (!guildID.matches("\\d+")) {
            return Util.replyToMessage(message, "Invalid guild ID format. Please provide a numeric guild ID.").then();
        }
		
		return Service.client.getGuildById(Snowflake.of(guildID))
	        .flatMap(guild -> guild.leave()
	        .then(Util.replyToMessage(message, "Server left.").then()))
            .onErrorResume(ClientException.isStatusCode(404), error -> Util.replyToMessage(message, "Cannot find guild with id " + guildID).then())
            .onErrorResume(error -> Util.replyToMessage(message, "An error occurred: " + error.getMessage()).then());
    
	}
	
}
