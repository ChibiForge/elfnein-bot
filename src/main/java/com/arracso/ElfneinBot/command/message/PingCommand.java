package com.arracso.ElfneinBot.command.message;

import java.time.Duration;

import com.arracso.ElfneinBot.util.Util;

import discord4j.core.object.entity.Message;
import discord4j.core.spec.MessageEditSpec;
import reactor.core.publisher.Mono;

public class PingCommand extends MessageCommand {

	private String area = "general";
	
	public PingCommand(){
		commandNames.add("ping");
		commandNames.add("pong");
		commandId = 0;
	}
	
	public PingCommand(String area){
		this.area = area;
		commandNames.add("ping");
		commandNames.add("pong");
		commandId = 0;
	}
	
	@Override
	public Mono<Void> execute(Message message) {
		String ans = "Ping!";
		if(message.getContent().toLowerCase().contains("ping")) ans = "Pong!";
		return Util.replyToMessage(message, ans).flatMap(messagePing -> editPing(message, messagePing)).then();
	}

	@SuppressWarnings("deprecation")
	private Mono<? extends Void> editPing(Message message, Message messagePing) {
		Duration ping = Duration.between(message.getTimestamp(),messagePing.getTimestamp());
		return messagePing.edit(MessageEditSpec.builder().content(messagePing.getContent()+" `" + ping.toMillis() + "ms`\n-# Command group: "+ area).build()).then();
	}
}
