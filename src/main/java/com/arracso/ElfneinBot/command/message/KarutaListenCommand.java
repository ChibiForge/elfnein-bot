package com.arracso.ElfneinBot.command.message;

import com.arracso.ElfneinBot.util.Global;
import com.arracso.ElfneinBot.util.Util;

import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;

public class KarutaListenCommand extends MessageCommand {
	
	public KarutaListenCommand() {
		this.commandId = 98;
	}
	
	@Override
	public Boolean check(Message message) {
		return message.getAuthor().get().getId().asString().equals(Global.KarutaID);
	}
	
	@Override
	public Mono<Void> execute(Message message) {
		Util.showEmbed(message);
		Util.showReply(message);
		return Mono.empty();
	}

}
