package com.arracso.ElfneinBot.command.message.reaction;

import com.arracso.ElfneinBot.util.Util;

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
			return message.getChannel().flatMap(channel -> channel.createMessage(cards)
				.withMessageReference(message.getId())).then();
		}catch(Exception e) {
			return message.getChannel().flatMap(channel -> channel.createMessage("Something went wrong! Please tell <@278957461120090113> to fix me!")
					.withMessageReference(message.getId())).then();
		}
		
	}
}
