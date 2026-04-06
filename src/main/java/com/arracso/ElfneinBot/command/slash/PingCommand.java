package com.arracso.ElfneinBot.command.slash;

import java.time.Duration;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import reactor.core.publisher.Mono;

public class PingCommand extends SlashCommand {

	public PingCommand(){}
	
	@Override
	public Mono<Void> execute(ChatInputInteractionEvent event) {
		return event.reply("Pong").then(editPing(event).then());
	}
	
	private Mono<? extends Void> editPing(ChatInputInteractionEvent event) {
		return event.getReply().flatMap(messagePing -> {
			Duration ping = Duration.between(event.getInteraction().getId().getTimestamp(),messagePing.getTimestamp());
			String updatedMsg = messagePing.getContent()+" `" + ping.toMillis() + "ms`";
			return event.editReply(updatedMsg).then();
		});
	}

	@Override
	public String getName() { return "ping"; }

}
