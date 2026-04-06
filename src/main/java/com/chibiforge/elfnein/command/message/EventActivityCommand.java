package com.chibiforge.elfnein.command.message;

import java.time.LocalDate;

import com.chibiforge.elfnein.util.Global;
import com.chibiforge.elfnein.util.Service;
import com.chibiforge.elfnein.util.Util;

import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;

public class EventActivityCommand extends MessageCommand {
	
	public EventActivityCommand(){
		commandId = Global.cmdIdChatting;
	}
	
	@Override
	public Boolean check(Message message) {		
		// Check if its not a bot
		return message.getAuthor().map(user -> !user.isBot()).orElse(false);
	}
	
	@Override
	public Mono<Void> execute(Message message) {
		// Check time
		LocalDate now = LocalDate.now();
		if(now.getMonthValue() == 12 || (now.getMonthValue() == 1 && now.getDayOfMonth() < 6))
			return activityXmasEvent(message);
		else if(now.getMonthValue() == 1 && now.getDayOfMonth() >= 14 && now.getDayOfMonth() < 20)
			return activityBowEvent(message);
		return Mono.empty();
	}
	
	private Mono<Void> activityXmasEvent(Message message) {
		return message.getAuthor().map(user -> {
			Integer res = Service.eventXmasService.updateCraftingProgression(user.getId().asString());
			if(res == 1) return Util.replyToMessage(message, "You finished crafting a wooden car. You have received 2 coins for your work.").then();
			if(res == 2) return Util.replyToMessage(message, "You finished crafting a wooden doll. You have received 12 coins for your work.").then();
			if(res == 3) return Util.replyToMessage(message, "You finished crafting a wooden marble run. You have received 8 coins for your work.").then();
			return Mono.empty();
		}).orElse(Mono.empty()).then();
	}
	
	private Mono<Void> activityBowEvent(Message message) {
		return message.getAuthor().map(user -> {
			Long bowId = Service.userInventoryService.checkForBows(user.getId().asString());
			if(bowId != null) return Util.replyToMessage(message,
				"You notice something on the ground. It's a bow!\n" +
				"**Collected:** *" + Service.userInventoryService.getItem(bowId).getNameAndIcon(false) + "*"
			).then();
			return Mono.empty();
		}).orElse(Mono.empty()).then();
	}
}
