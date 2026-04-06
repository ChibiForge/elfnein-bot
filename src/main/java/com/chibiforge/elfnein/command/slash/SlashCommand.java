package com.chibiforge.elfnein.command.slash;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import reactor.core.publisher.Mono;

public abstract class SlashCommand {

	public Boolean check(ChatInputInteractionEvent event) {
		return getName().equalsIgnoreCase(event.getCommandName());
	}
	
    public abstract String getName();
    
	public abstract Mono<Void> execute(ChatInputInteractionEvent event);
}