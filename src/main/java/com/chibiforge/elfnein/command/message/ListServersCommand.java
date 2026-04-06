package com.chibiforge.elfnein.command.message;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.chibiforge.elfnein.util.Global;
import com.chibiforge.elfnein.util.Service;
import com.chibiforge.elfnein.util.Util;

import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Message;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class ListServersCommand extends MessageCommand {
	
	public ListServersCommand(){
		commandNames.add("listservers");
		commandNames.add("ls");
		commandId = Global.cmdAdmin;
	}
	
	@Override
	public Mono<Void> execute(Message message) {
		if(!message.getAuthor().isPresent())
			return Util.replyToMessage(message, "Something went wrong! Cannot retrieve user id. Please tell <@278957461120090113> to fix me!").then();
		
		// Get guilds
		List<Guild> guilds = new ArrayList<Guild>();		
		Service.client.getGuilds()
			.flatMap(guild -> Flux.just(guild))
			.subscribe(guild -> guilds.add(guild));
		
		List<String> guildsStr = new ArrayList<String>();
		for(Guild guild:guilds) {
			String guildId = guild.getId().asString();
			String guildName = guild.getName();
			
			//Member owner = guild.getOwner().block();
			//String ownerID = owner.getId().asString();
			//String ownerNick = owner.getNickname().isPresent()?owner.getNickname().get():"";
			
			Integer nMembers = guild.getMemberCount();
			
			Instant joinInstant = guild.getJoinTime();
			String joinTime = "";

			if (joinInstant != null) {
			    LocalDateTime joinDateTime = LocalDateTime.ofInstant(joinInstant, ZoneId.systemDefault());
			    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			    joinTime = joinDateTime.format(formatter);
			}
			
			//guildsStr.add(guildId + "\t" + guildName + "\t" + nMembers + "\t" + joinTime + "\t" + ownerID + "\t" + ownerNick);
			guildsStr.add("`" + joinTime + "` - " + guildName + " (" + guildId + ") - " + nMembers);
		}
		
		guildsStr.sort(null);
		
		return Service.paginationService.paginate("Server List","**Number of servers: "+ guilds.size()+"**\n",guildsStr, message);
	}
}
