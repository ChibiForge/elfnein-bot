package com.arracso.ElfneinBot.command.message;

import java.time.Instant;

import com.arracso.ElfneinBot.util.Global;
import com.arracso.ElfneinBot.util.Locator;
import com.arracso.ElfneinBot.util.Locator.Location;
import com.arracso.ElfneinBot.util.Service;
import com.arracso.ElfneinBot.util.Util;
import com.arracso.ElfneinBot.util.DateSolver.DateSolver;
import com.arracso.ElfneinBot.util.DateSolver.DateSolver.Solution;

import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

public class KarutaDateCommand extends MessageCommand {
	
	private String trigger;
	private Location[] triggerLocation;
	private String authorID;
	
	public KarutaDateCommand() {
		this.trigger = Global.kviT;
		this.triggerLocation = Global.kviL;
		this.authorID = Global.KarutaID;
		this.commandId = Global.cmdIdDate;
	}
	
	@Override
	public Boolean check(Message message) {
		if(!authorID.isEmpty()) {
			// Check if message has author
			if(!message.getAuthor().isPresent()) return false;
			// Check if message is from Karuta
			if(!message.getAuthor().get().getId().asString().equals(authorID)) return false;
		}
		// Check trigger
		return Locator.get(message, triggerLocation).startsWith(trigger);
	}
	
	
	@Override
	public Mono<Void> execute(Message message) {		
		// Check if banned
		if(message.getReferencedMessage().isPresent()) {
			Message aux = message.getReferencedMessage().get();
			if(aux.getAuthor().isPresent()) {
				if(Service.userService != null) {
					String userId = aux.getAuthor().get().getId().asString();
					if(Service.userService.isBanned(userId)) {
						String reason = "" + Service.userService.getReasonOfBan(userId);
						return Util.replyToMessage(message, reason).then();
						
					}
				}
			}
		}
		
		// Get saved solution
		Solution sol = DateSolver.getSavedSolution(message);
		if(sol != null) return Util.replyToMessage(message, DateSolver.getSolutionEmbed(sol)).then();
		
		// Get uptime
		Instant uptime = DateSolver.getUptime(message);
		if(uptime != null) return Util.replyToMessage(message, "Give me more time! Don't spam!").then();
		
		return Util.replyToMessage(message, Global.loadingGIF)
			.flatMap(messageRes ->  Mono.fromCallable(
				() -> DateSolver.executeSolve(message, messageRes)
			).subscribeOn(Schedulers.parallel())
			.flatMap(mono -> mono.then())
		);
	}

}
