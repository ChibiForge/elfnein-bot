package com.arracso.ElfneinBot.command.message;

import com.arracso.ElfneinBot.util.Global;
import com.arracso.ElfneinBot.util.Service;

import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;

public abstract class MessageCommand {
	
	String[] prefixes = {"Elfnein ","e!","e.","E!","E.","<@"+Global.ElfneinID+">"};
	String commandName;
	Integer commandId = 0;
	
	public Integer getCommandId() {
		return commandId;
	}
	
	public Boolean isActive(Message message) {
		if(Service.commandService != null) return Service.commandService.isActive(commandId, message);
		return false;
	}
	
	public Boolean check(Message message) {
		Boolean isCommand = false;
		int i = 0;
		while(i<prefixes.length && !isCommand) {
			if(message.getContent().startsWith(prefixes[i])) {
				String messageAux = message.getContent().replaceFirst("^"+prefixes[i], "").trim();
				if(messageAux.toLowerCase().equals(commandName) || messageAux.toLowerCase().startsWith(commandName+" ")) isCommand = true;
			}
			i++;
		}
		return isCommand;
	}
	
	public abstract Mono<Void> execute(Message message);

}