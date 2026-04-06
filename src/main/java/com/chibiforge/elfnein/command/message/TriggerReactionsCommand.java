package com.chibiforge.elfnein.command.message;

import java.util.HashSet;
import java.util.Set;

import com.chibiforge.elfnein.util.Locator;
import com.chibiforge.elfnein.util.Locator.Location;

import discord4j.core.object.emoji.Emoji;
import discord4j.core.object.entity.Message;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class TriggerReactionsCommand extends MessageCommand {
	
	private String trigger;
	private Location[] triggerLocation;
	private Set<Emoji> reactions;
	private String authorID;
	
	public TriggerReactionsCommand(String trigger, Location[] triggerLocation, Emoji[] reactions) {
		this.trigger = trigger;
		this.triggerLocation = triggerLocation;
		this.reactions = new HashSet<Emoji>();
		for(Emoji reaction:reactions) this.reactions.add(reaction);
		this.authorID = "";
	}
	
	public TriggerReactionsCommand(String trigger, Location[] triggerLocation, Emoji[] reactions, String authorID) {
		this.trigger = trigger;
		this.triggerLocation = triggerLocation;
		this.reactions = new HashSet<Emoji>();
		for(Emoji reaction:reactions) this.reactions.add(reaction);
		this.authorID = authorID;
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
		return Locator.get(message, triggerLocation).startsWith(trigger);
	}
	
	@Override
	public Mono<Void> execute(Message message) {
		return Flux.fromIterable(reactions).flatMap(reaction -> message.addReaction(reaction).then()).then();
	}

}
