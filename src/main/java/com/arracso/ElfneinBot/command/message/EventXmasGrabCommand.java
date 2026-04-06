package com.arracso.ElfneinBot.command.message;

import java.time.LocalDate;
import java.util.Optional;

import com.arracso.ElfneinBot.util.Global;
import com.arracso.ElfneinBot.util.Service;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import reactor.core.publisher.Mono;

public class EventXmasGrabCommand extends MessageCommand {

	private Snowflake authorID;
	
	public EventXmasGrabCommand() {
		this.authorID = Snowflake.of(Global.KarutaID);
		this.commandId = Global.eventXmasBase;
	}
	
	@Override
	public Boolean check(Message message) {
		Optional<User> authorOpt = message.getAuthor();
		// Check if message has author
		if(!authorOpt.isPresent()) return false;
		// Check if message is from Karuta
		if(!authorOpt.get().getId().equals(authorID)) return false;
		// Check trigger
		if(!message.getContent().contains("took the")) return false;
		// Check time
		LocalDate now = LocalDate.now();
		if(now.getMonthValue() != 12 && (now.getMonthValue() != 1 || now.getDayOfMonth() > 5))
			return false;
		
		return true;
	}
	
	
	@Override
	public Mono<Void> execute(Message message) {		
		String userId = message.getContent().split("<@")[1].split(">")[0];
		String item = Service.eventXmasService.giveRandomItem(userId);
		return message.getChannel().flatMap(channel -> channel.createMessage("<@" + userId + "> received a " + item +"!").then()).then();
	}

}
