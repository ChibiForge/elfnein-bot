package com.arracso.ElfneinBot.listener;

import java.util.Set;

//import org.springframework.stereotype.Service;

import com.arracso.ElfneinBot.command.message.interaction.AreaButtonCommand;
import com.arracso.ElfneinBot.command.message.interaction.ButtonCommand;

import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

//@Service
public class ButtonInteractionListener implements EventListener<ButtonInteractionEvent> {
	
	// Setup commands //
	private static final Set<ButtonCommand> commandsTest = Set.of(
		new AreaButtonCommand()
	);
	
	private static final Set<ButtonCommand> commands = commandsTest;
	
    @Override
    public Class<ButtonInteractionEvent> getEventType() {
        return ButtonInteractionEvent.class;
    }
    
    @Override
    public Mono<Void> execute(ButtonInteractionEvent event) {
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