package com.chibiforge.elfnein.command.message;

import java.util.List;

import com.chibiforge.elfnein.util.Global;
import com.chibiforge.elfnein.util.Service;
import com.chibiforge.elfnein.util.Util;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.rest.http.client.ClientException;
import reactor.core.publisher.Mono;

public class GetInviteServerCommand extends MessageCommand {
	
	public GetInviteServerCommand(){
		commandNames.add("getinvite");
		commandNames.add("getinv");
		commandNames.add("gi");
		commandId = Global.cmdAdmin;
	}
	
	@Override
	public Mono<Void> execute(Message message) {
		if(!message.getAuthor().isPresent())
			return Util.replyToMessage(message, "Something went wrong! Cannot retrieve user id. Please tell <@" + Service.properties.getDeveloperID() + "> to fix me!").then();
		
		// Get command parameters
		List<String> commandParameters = getParameters(message);
		if(commandParameters.isEmpty())
			return Util.replyToMessage(message, "*Command usage:* `leaveserver serverID`").then();
		
		String guildID = commandParameters.get(0).strip();
		
		// Validate the guild ID format
        if (!guildID.matches("\\d+")) {
            return Util.replyToMessage(message, "Invalid guild ID format. Please provide a numeric guild ID.").then();
        }
        
        return Service.client.getGuildById(Snowflake.of(guildID))
                .flatMap(guild -> guild.getChannels().ofType(TextChannel.class).next()
                    .flatMap(channel -> channel.createInvite().withMaxAge(86400).withMaxUses(1).map(invite -> "https://discord.gg/" + invite.getCode()))
                    .switchIfEmpty(Mono.error(new RuntimeException("No text channel found")))
                )
                .flatMap(inviteLink -> Util.replyToMessage(message, inviteLink).then())
                .onErrorResume(ClientException.isStatusCode(404), error -> Util.replyToMessage(message, "Cannot find guild with id " + guildID).then())
                .onErrorResume(error -> Util.replyToMessage(message, "Cannot generate invite for guild with id " + guildID).then());
	}
	

}
