package com.arracso.ElfneinBot.listener;

import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.arracso.ElfneinBot.command.message.*;

import discord4j.core.event.domain.message.MessageUpdateEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class MessageUpdateListener implements EventListener<MessageUpdateEvent> {
	
	// Setup commands //
	private static final Set<MessageCommand> commands = new HashSet<>();
	static {
		commands.add(new KarutaCollectionListMessageCommand());
		//commands.add(new KarutaListenCommand());
	}
	
    @Override
    public Class<MessageUpdateEvent> getEventType() {
        return MessageUpdateEvent.class;
    }
    
    @Override
    public Mono<Void> execute(MessageUpdateEvent event) {
    	return event.getMessage()
    		.flatMap(message -> Flux.fromIterable(commands)
    			.filter(command -> command.check(message))
    			.flatMap(command -> command.execute(message)).next())
    		.then();      		
    }
}