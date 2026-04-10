package com.chibiforge.elfnein.listener;

import java.util.Set;

import org.springframework.stereotype.Service;

import com.chibiforge.elfnein.command.message.*;

import discord4j.core.event.domain.message.MessageUpdateEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class MessageUpdateListener implements EventListener<MessageUpdateEvent> {
	

	private static final Set<MessageCommand> commandsActivity = Set.of(
		new NodeDataCommand(true)
	);
	
	@SuppressWarnings("unused")
	private static final Set<MessageCommand> commandsTest = Set.of(
		new KarutaCollectionListMessageCommand()/*,
		new KarutaListenCommand()*/
	);
	
	private static final Set<MessageCommand> commands = commandsActivity;
	
    @Override
    public Class<MessageUpdateEvent> getEventType() {
        return MessageUpdateEvent.class;
    }
    
    @Override
    public Mono<Void> execute(MessageUpdateEvent event) {
    	return event.getMessage()
    		.flatMap(message -> Flux.fromIterable(commands)
    			.filter(command -> command.isActive(message))
    			.filter(command -> command.check(message))
    			.flatMap(command -> command.execute(message)).next())
    		.onErrorComplete().then();      		
    }
}