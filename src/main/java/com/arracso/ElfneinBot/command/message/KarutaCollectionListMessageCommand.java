package com.arracso.ElfneinBot.command.message;


import java.util.List;

import com.arracso.ElfneinBot.util.Global;
import com.arracso.ElfneinBot.util.Locator;
import com.arracso.ElfneinBot.util.Util;

import discord4j.core.object.entity.Message;
import discord4j.core.spec.MessageEditSpec;
import reactor.core.publisher.Mono;

public class KarutaCollectionListMessageCommand extends MessageCommand {
	
	@Override
	public Boolean check(Message message) {
		// Check if message has author
		if(!message.getAuthor().isPresent()) return false;
		// Check if message is from Karuta
		if(!message.getAuthor().get().getId().asString().equals(Global.KarutaID)) return false;
		// Check trigger
		if(!Locator.get(message, Global.kcL).startsWith(Global.kcT)) return false;
		// Check if message has reactions
		if(message.getReactors(Global.kcR[0]).count().block() <= 1) return false;
		
		return true;
	}
	
	@SuppressWarnings("deprecation") //TODO
	@Override
	public Mono<Void> execute(Message message) {
		try {
			// Get message
			Message resMessage = Util.getReplies(message).filter(msg -> {
				if(msg.getAuthor().isPresent())
					return msg.getAuthor().get().getId().asString().equals(Global.ElfneinID);
				return false;
			}).blockFirst();
			if(resMessage == null) return Mono.empty();
			// Get cards
			String cards = resMessage.getContent();
			// Get new cards
			List<String> newCards = Util.getCards(message.getEmbeds().get(0).getDescription().get());
			if(newCards.isEmpty()) return Mono.empty();
			// Fuse cards
			for(String card:newCards)
				if(!cards.contains(card))
					cards = cards.concat(", "+card);
			// Edit message
			return resMessage.edit(MessageEditSpec.builder().content(cards).build()).then();
		}catch(Exception e) {
			System.out.println(e.getStackTrace());
			return Mono.empty();
		}
		
		
	}
	
	


}
