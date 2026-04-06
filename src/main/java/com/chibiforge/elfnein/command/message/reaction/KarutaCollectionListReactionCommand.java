package com.chibiforge.elfnein.command.message.reaction;

import com.chibiforge.elfnein.util.Util;

import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;

public class KarutaCollectionListReactionCommand extends ReactionCommand {
	
	public KarutaCollectionListReactionCommand(){
		custom = false;
		emojiName = "🖨️";
	}
	
	@Override
	public Mono<Void> execute(Message message) {
		try {
			String cards = Util.substring(Util.getCards(message.getEmbeds().get(0).getDescription().get()).toString(), 1, -1);
			if(cards.isEmpty()) return Mono.empty();		
			return Util.replyToMessage(message, cards).then();
		}catch(Exception e) {
			return Util.replyToMessage(message, "Something went wrong! Please tell <@278957461120090113> to fix me!").then();
		}
		
	}
}
