package com.arracso.ElfneinBot.command.message;

import com.arracso.ElfneinBot.util.Global;
import com.arracso.ElfneinBot.util.Util;

import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;

public class KarutaListenCommand extends MessageCommand {
	
	@Override
	public Boolean check(Message message) {
		return message.getAuthor().get().getId().asString().equals(Global.KarutaID);
	}
	
	@Override
	public Mono<Void> execute(Message message) {
		Util.showEmbed(message);
		return Mono.empty();
	}

}
