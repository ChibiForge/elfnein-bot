package com.arracso.ElfneinBot.command.message;

import java.util.List;

import com.arracso.ElfneinBot.model.ShogunInfo;
import com.arracso.ElfneinBot.util.Global;
import com.arracso.ElfneinBot.util.Service;
import com.arracso.ElfneinBot.util.Util;

import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;

public class KarutaSetShogunCommand extends MessageCommand {

	public KarutaSetShogunCommand(){
		commandNames.add("setshogun");
		commandNames.add("ssl");
		commandId = Global.cmdIdShogunInfo;
	}
	
	@Override
	public Mono<Void> execute(Message message) {
		List<String> parameter = getParameters(message);

		if(parameter.size() != 2) return Util.replyToMessage(message, "Usage: e.setshogun [ID] [none|ls]").then(); 
		
		String userId = "";
		if(parameter.get(0).startsWith("<@")) userId = parameter.get(0).split("@")[1].split(">")[0];
		else userId = parameter.get(0);
		
		String side = parameter.get(1);
		Integer s = 0;

		if(side.equals("0") || side.toLowerCase().equals("none")) s = 0;
		if(side.equals("1") || side.toLowerCase().equals("ls")) s = 1;
		if(side.equals("2") || side.toLowerCase().equals("ll")) s = 2;
		if(side.equals("3") || side.toLowerCase().equals("tre")) s = 3;
		
		ShogunInfo shogunInfo = Service.karutaService.getShogunInfo(userId);
		if(shogunInfo == null) {
			shogunInfo = new ShogunInfo();
			shogunInfo.setId(userId);
		}
		
		shogunInfo.setSide(s);
		
		Service.karutaService.setShogunInfo(shogunInfo);
		
		return Util.replyToMessage(message, "Shogun location registered! Value: " + (s == 1?"LS":(s == 2?"LL":(s == 3?"TRE":"NONE")))).then();

	}
	
}
