package com.chibiforge.elfnein.command.message;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.chibiforge.elfnein.util.Global;
import com.chibiforge.elfnein.util.Service;
import com.chibiforge.elfnein.util.Util;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.rest.http.client.ClientException;
import reactor.core.publisher.Mono;

public class GetServerInfoCommand extends MessageCommand {
	
	public GetServerInfoCommand(){
		commandNames.add("serverinfo");
		commandId = Global.cmdAdmin;
	}
	
	@Override
	public Mono<Void> execute(Message message) {
		if(!message.getAuthor().isPresent())
			return Util.replyToMessage(message, "Something went wrong! Cannot retrieve user id. Please tell <@" + Service.properties.getDeveloperID() + "> to fix me!").then();
		
		// Get command parameters
		List<String> commandParameters = getParameters(message);
		if(commandParameters.isEmpty())
			return Util.replyToMessage(message, "*Command usage:* `serverinfo serverID`").then();
		
		String guildID = commandParameters.get(0).strip();
		
		// Validate the guild ID format
        if (!guildID.matches("\\d+")) {
            return Util.replyToMessage(message, "Invalid guild ID format. Please provide a numeric guild ID.").then();
        }
        
        Service.client.getGuildById(Snowflake.of(guildID));
        
        return Service.client.getGuildById(Snowflake.of(guildID))
                .flatMap(guild -> {
        			String guildName = guild.getName();
        			
        			Member owner = guild.getOwner().block();
        			String ownerID = owner.getId().asString();
        			String ownerNick = owner.getNickname().isPresent()?owner.getNickname().get():"";
        			
        			Integer nMembers = guild.getMemberCount();
        			
        			Instant joinInstant = guild.getJoinTime();
        			String joinTime = "";

        			if (joinInstant != null) {
        			    LocalDateTime joinDateTime = LocalDateTime.ofInstant(joinInstant, ZoneId.systemDefault());
        			    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        			    joinTime = joinDateTime.format(formatter);
        			}
        			return Util.replyToMessage(message, "**Guild:** " + guildName + " (" + guildID + ")\n" +
    					"**Joined:** " + joinTime + " **Members:** " + nMembers + "\n" +
    					"**Owner:** " + ownerNick + "(" + ownerID + ")"
        			).then();
                })
                .onErrorResume(ClientException.isStatusCode(404), error -> Util.replyToMessage(message, "Cannot find guild with id " + guildID).then());
	}
	

}
