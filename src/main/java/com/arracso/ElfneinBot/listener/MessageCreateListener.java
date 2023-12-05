package com.arracso.ElfneinBot.listener;

import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.arracso.ElfneinBot.command.message.*;
import com.arracso.ElfneinBot.util.Global;

import discord4j.core.event.domain.message.MessageCreateEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class MessageCreateListener implements EventListener<MessageCreateEvent> {
	
	// Setup commands //
	private static final Set<MessageCommand> commands = new HashSet<>();
	static {
		
		// Normal Commands
		commands.add(new HelpCommand());
		commands.add(new PingCommand());
		commands.add(new SayCommand());
		// Trigger Reactions Commands
		commands.add(new TriggerReactionsCommand(Global.kcT,Global.kcL,Global.kcR,Global.KarutaID));
		//commands.add(new TriggerReactionsCommand(Global.kiT,Global.kiL,Global.kiR,Global.KarutaID));
		//commands.add(new TriggerReactionsCommand(Global.kbiT,Global.kbiL,Global.kbiR,Global.KarutaID));
		// Trigger Message Commands
		commands.add(new TriggerMessageCommand(Global.gnT,Global.gnA,Global.gnC));
		commands.add(new TriggerMessageCommand(Global.gmT,Global.gmA,Global.gmC));
		commands.add(new TriggerMessageCommand(Global.bonkT,Global.bonkA,Global.bonkC));
		commands.add(new TriggerMessageCommand(Global.madT,Global.madA,Global.madC));
		commands.add(new TriggerMessageCommand(Global.fightT,Global.fightA,Global.fightC));
		commands.add(new TriggerMessageCommand(Global.danceT,Global.danceA,Global.danceC));
		commands.add(new TriggerMessageCommand(Global.loveT,Global.loveA,Global.loveC));
		// Other
		commands.add(new KarutaDateCommand());
		commands.add(new KarutaSolveDateCommand());
		commands.add(new KarutaMapDateCommand());		
	}
	
    @Override
    public Class<MessageCreateEvent> getEventType() {
        return MessageCreateEvent.class;
    }
    
    @Override
    public Mono<Void> execute(MessageCreateEvent event) {
        return Mono.just(event.getMessage())
        	.flatMap(message -> Flux.fromIterable(commands)
        		.filter(command -> command.isActive(message))
        		.filter(command -> command.check(message))
        		.flatMap(command -> command.execute(message)).next())
        	.then();
    }
}