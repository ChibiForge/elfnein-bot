package com.arracso.ElfneinBot.command.message.interaction;

import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import reactor.core.publisher.Mono;

public abstract class ButtonCommand {
	
	String buttonCmdId = "";
	
	public Boolean check(ButtonInteractionEvent event) {
		return event.getCustomId().startsWith(buttonCmdId + ":");
	}
	
	public abstract Mono<Void> execute(ButtonInteractionEvent event);
}