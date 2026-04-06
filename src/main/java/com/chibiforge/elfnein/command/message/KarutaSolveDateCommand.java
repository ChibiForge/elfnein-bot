package com.chibiforge.elfnein.command.message;

import java.time.Instant;

import com.chibiforge.elfnein.util.Global;
import com.chibiforge.elfnein.util.Util;
import com.chibiforge.elfnein.util.DateSolver.DateSolver;
import com.chibiforge.elfnein.util.DateSolver.DateSolver.Solution;

import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

public class KarutaSolveDateCommand extends MessageCommand {

	public KarutaSolveDateCommand(){
		this.commandNames.add("solve");
		commandId = Global.cmdIdSolve;
	}
	
	@Override
	public Mono<Void> execute(Message message) {
		// Check refMessage
		if(!message.getReferencedMessage().isPresent())
			return Util.replyToMessage(message, "You need to reply to a dating minigame.").then();
		
		Message refMessage = message.getReferencedMessage().get(); 
		
		if(refMessage.getEmbeds().size()==0)
			return Util.replyToMessage(message,"You need to reply to a dating minigame.").then();
		
		if(!refMessage.getEmbeds().get(0).getImage().isPresent())
			return Util.replyToMessage(message,"You need to reply to a dating minigame.").then();
		
		// Delete message
		message.delete().onErrorResume(t -> {
			System.out.println("ERROR SOLVE DELETE");
			System.out.println(t.getMessage());
			return Mono.empty();
		}).subscribe();
		
		// Get saved solution
		Solution sol = DateSolver.getSavedSolution(refMessage);
		if(sol != null) return Util.replyToMessage(refMessage, DateSolver.getSolutionEmbed(sol)).then();
		
		// Get uptime
		Instant uptime = DateSolver.getUptime(refMessage);
		if(uptime != null) return Util.replyToMessage(refMessage, "Give me more time! Don't spam!").then();
		
		return Util.replyToMessage(refMessage, Global.loadingGIF)
			.flatMap(messageRes -> Mono.fromCallable(
				() -> DateSolver.executeSolve(refMessage, messageRes)
			).subscribeOn(Schedulers.parallel())
			.flatMap(mono -> mono.then())
		);
	}

}
