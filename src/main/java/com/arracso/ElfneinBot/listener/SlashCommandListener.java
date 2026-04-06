package com.arracso.ElfneinBot.listener;

import java.util.Set;

//import org.springframework.stereotype.Service;

import com.arracso.ElfneinBot.command.slash.*;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

//@Service
public class SlashCommandListener implements EventListener<ChatInputInteractionEvent> {
	
	// Setup commands //
	private static final Set<SlashCommand> commandsSolveAndLoop = Set.of(
		new PingCommand()
		//new CheckPermsCommand()
	);
	
	private static final Set<SlashCommand> commands =commandsSolveAndLoop;
	
	
    @Override
    public Class<ChatInputInteractionEvent> getEventType() {
        return ChatInputInteractionEvent.class;
    }
    
    @Override
    public Mono<Void> execute(ChatInputInteractionEvent event) {
    	return Flux.fromIterable(commands)
        	.filter(command -> command.check(event))
        	.flatMap(command -> command.execute(event))
        	.next()
        	.onErrorResume(error -> {
        		error.printStackTrace();
        		return Mono.empty();
        	})
        	.onErrorComplete().then();
    }

}