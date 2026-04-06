package com.arracso.ElfneinBot.command.message.reaction;

import com.arracso.ElfneinBot.util.Global;

import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.object.emoji.Emoji;
import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;

public abstract class ReactionCommand {
	
	Boolean custom;
	Boolean animated;
	String emojiID;
	String emojiName;
	
	public Boolean check(ReactionAddEvent event) {
		// Discard bots
		if(event.getUser().block().isBot()) return false;
		
		// Discard reactions not added by Elfnein
		if(event.getMessage().block().getReactors(event.getEmoji())
			.filter(user -> user.isBot() && user.getId().asString().equals(Global.ElfneinID))
			.collectList().block()
			.size() == 0) return false;
		
		// Check if reaction is the needed
		if(custom && event.getEmoji().asCustomEmoji().isPresent())
			return  event.getEmoji().asCustomEmoji().get().equals(Emoji.custom(Snowflake.of(emojiID),emojiName, animated));
		else if (!custom && event.getEmoji().asUnicodeEmoji().isPresent())
			return event.getEmoji().asUnicodeEmoji().get().equals(Emoji.unicode(emojiName));
		
		return false;
	}
	
	public abstract Mono<Void> execute(Message message);
}