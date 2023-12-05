package com.arracso.ElfneinBot.listener;

import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.arracso.ElfneinBot.command.message.reaction.KarutaCollectionListReactionCommand;
import com.arracso.ElfneinBot.command.message.reaction.ReactionCommand;

import discord4j.core.event.domain.message.ReactionAddEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ReactionAddListener implements EventListener<ReactionAddEvent> {
	
	// Setup commands //
	private static final Set<ReactionCommand> commands = new HashSet<>();
	static {
		commands.add(new KarutaCollectionListReactionCommand());
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
        	.then();
    }
}