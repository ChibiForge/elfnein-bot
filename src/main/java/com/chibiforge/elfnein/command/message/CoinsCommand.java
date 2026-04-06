package com.chibiforge.elfnein.command.message;

import java.util.List;

import com.chibiforge.elfnein.util.Global;
import com.chibiforge.elfnein.util.Service;
import com.chibiforge.elfnein.util.Util;

import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;

public class CoinsCommand extends MessageCommand {
	
	public CoinsCommand(){
		commandNames.add("coins");
		commandId = Global.cmdAdmin;
	}
	
	@Override
	public Mono<Void> execute(Message message) {
		List<String> parameter = getParameters(message);
		if(parameter.size()!=3)	return Util.replyToMessage(message, "Valid usages: `e.coins [add|remove] [@USER|ID] [AMOUNT]`").then();
		
		// Get the sign
		String op = parameter.get(0).toLowerCase();
		boolean add;
		if (op.equals("add")) add = true;
		else if (op.equals("remove")) add = false;
		else return Util.replyToMessage(message, "First parameter must be `add` or `remove`!").then();
		
		// Get the amount
		int amount;
		try { amount = Integer.parseInt(parameter.get(2)); }
		catch(Exception e) { return Util.replyToMessage(message, "Invalid amount!").then(); }
		if(amount <= 0) return Util.replyToMessage(message, "Invalid amount!").then();
		
		// Get the id
		String targetId = Util.parseId(parameter.get(1));
		Long coinItemId = 0L;
		
		// Apply
		int delta = add ? amount : -amount;
		return Mono.fromCallable(() -> Service.userInventoryService.changeItemQuantity(targetId, coinItemId, delta))
	    .subscribeOn(reactor.core.scheduler.Schedulers.boundedElastic())
	    .flatMap(change -> {
	        String text = String.format("Updated coins for <@%s>: %d🪙 → %d🪙 (%+d)", change.userId(), change.before(), change.after(), change.delta());
	        return Util.replyToMessage(message, text).then();
	    })
	    .onErrorResume(ex -> Util.replyToMessage(message, "Cannot do that: " + ex.getMessage()).then())
	    .then();
	}
}
