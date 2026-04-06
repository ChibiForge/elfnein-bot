package com.arracso.ElfneinBot.command.message;

import com.arracso.ElfneinBot.util.Global;
import com.arracso.ElfneinBot.util.Service;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;

public class EventTOTCoinActivityCommand extends MessageCommand {

	private String authorID;
	
	public EventTOTCoinActivityCommand() {
		this.authorID = Global.KarutaID;
		commandNames.add("house");
		commandNames.add("home");
		this.commandId = Global.eventTOTDrop;
	}
	
	@Override
	public Boolean check(Message message) {
		if(!authorID.isEmpty()) {
			// Check if message has author
			if(!message.getAuthor().isPresent()) return false;
			// Check if message is from Karuta
			if(!message.getAuthor().get().getId().asString().equals(authorID)) return false;
		}
		// Check trigger
		return message.getContent().contains("is dropping");
	}
	
	
	@Override
	public Mono<Void> execute(Message message) {		
		String userId = message.getContent().split("<@")[1].split(">")[0];
		String guildId = message.getGuildId().map(Snowflake::asString).orElse("");
		Service.eventTOTService.giveCoin(guildId,userId);
		return Mono.empty();
	}

}
