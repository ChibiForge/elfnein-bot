package com.arracso.ElfneinBot.command.message;

import com.arracso.ElfneinBot.util.SheetsServiceUtil;
import com.arracso.ElfneinBot.util.Util;

import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;

public class KarutaMySlotsCommand extends MessageCommand {
	
	public KarutaMySlotsCommand(){
		commandNames.add("myslots");
		commandNames.add("ms");
		commandNames.add("myqueue");
		commandNames.add("mq");
	}
	
	@Override
	public Mono<Void> execute(Message message) {		
		// Get Iron data
		String [][] data = SheetsServiceUtil.getData("1U_BMF1e9yNge_7GuJEKNnYiq4i4ku0uPObx72AvRcRs","Slots","A1:A39");
		
		if(data == null) 
			return Util.replyToMessage(message, "Cannot access data. Please tell <@278957461120090113> to fix me!").then();
		
		// Get author
		if(!message.getAuthor().isPresent())
			return Util.replyToMessage(message, "Something went wrong! Cannot retrieve user id. Please tell <@278957461120090113> to fix me!").then();
		
		String userId = message.getAuthor().get().getId().asString();
		
		String queues = "";
		for(int i = 0;i<data.length; i++) {
			if(data[i][0].contains(userId)) queues = queues + data[i][0] + "\n";
		}
		 
		if(queues.equals("")) 
			return Util.replyToMessage(message, "You have no queues").then();
		
		return Util.replyToMessage(message, queues).then();
	}
	
	
}
