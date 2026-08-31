package com.chibiforge.elfnein.command.message;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.chibiforge.elfnein.util.Global;
import com.chibiforge.elfnein.util.Service;

import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;

public abstract class MessageCommand {
	
	String[] prefixes = {"Elfnein ","e!","e.","E!","E.","<@"+Global.ElfneinID+">"};
	List<String> commandNames = new ArrayList<String>();
	Integer commandId = 99;
	
	public Integer getCommandId() {
		return commandId;
	}
	
	public Boolean isActive(Message message) {
		if(Service.commandService != null) return Service.commandService.isActive(commandId, message);
		return false;
	}
	
	public Boolean check(Message message) {
		// Check prefix
		String messageStr = message.getContent();
        String prefix = findPrefix(messageStr);
        if (prefix == null) return false;
        // Check command
        String withoutPrefix = messageStr.substring(prefix.length()).strip();
        String commandName = findCommandName(withoutPrefix);
        if (commandName == null) return false;
        
		return true;
	}
	
	public abstract Mono<Void> execute(Message message);
	
	public List<String> getParameters(Message message) {
        String withoutCommand = stripCommand(message);
        if(withoutCommand.isBlank()) return new ArrayList<String>();
        return Arrays.asList(withoutCommand.split("\\s+"));
    }
	
	public String stripCommand(Message message) {
		String messageStr = message.getContent();
        String prefix = findPrefix(messageStr);
        String withoutPrefix = messageStr.substring(prefix.length());
        String commandName = findCommandName(withoutPrefix);
        return withoutPrefix.substring(commandName.length()).trim();
	}
	
	public String getCommandName(Message message) {
		String messageStr = message.getContent();
        String prefix = findPrefix(messageStr);
        String withoutPrefix = messageStr.substring(prefix.length());
        return findCommandName(withoutPrefix);
	}
	
    private String findPrefix(String message) {
        for (String prefix : prefixes) 
        	if (message.toLowerCase().startsWith(prefix.toLowerCase())) return prefix;
        return null;
    }

    private String findCommandName(String message) {
        for (String commandName : commandNames)
        	if (message.toLowerCase().startsWith(commandName.toLowerCase() + " ") || message.toLowerCase().equals(commandName)) return commandName;
        return null;
    }

}