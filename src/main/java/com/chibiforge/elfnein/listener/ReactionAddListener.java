package com.chibiforge.elfnein.listener;

import java.util.HashSet;
import java.util.Set;

import com.chibiforge.elfnein.command.message.reaction.ReactionCommand;

import discord4j.core.event.domain.message.ReactionAddEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

//@Service
public class ReactionAddListener implements EventListener<ReactionAddEvent> {
	
	// Setup commands //
	private static final Set<ReactionCommand> commands = new HashSet<>();
	static {
		//commands.add(new KarutaCollectionListReactionCommand());
	}
	
    @Override
    public Class<ReactionAddEvent> getEventType() {
        return ReactionAddEvent.class;
    }
    
    @Override
    public Mono<Void> execute(ReactionAddEvent event) {
        return event.getMessage().flatMap(message -> Flux.fromIterable(commands)
        		.filter(command -> command.check(event))
        		.flatMap(command -> command.execute(message)).next())
        	.onErrorComplete().then();
    }
}