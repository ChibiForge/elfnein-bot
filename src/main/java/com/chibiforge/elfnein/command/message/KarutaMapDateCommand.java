package com.chibiforge.elfnein.command.message;

import java.awt.image.BufferedImage;
import java.net.URI;
import java.net.URL;

import javax.imageio.ImageIO;

import com.chibiforge.elfnein.util.Global;
import com.chibiforge.elfnein.util.Util;
import com.chibiforge.elfnein.util.DateSolver.DateSolver;

import discord4j.core.object.entity.Message;
import discord4j.core.spec.MessageEditSpec;
import reactor.core.publisher.Mono;

public class KarutaMapDateCommand extends MessageCommand {

	public KarutaMapDateCommand(){
		commandNames.add("map");
		commandId = Global.cmdIdMap;
	}
	
	@Override
	public Mono<Void> execute(Message message) {		
		// Check refMessage
		if(!message.getReferencedMessage().isPresent())
			return Util.replyToMessage(message, "You need to reply to a dating minigame.").then();
		
		Message refMessage = message.getReferencedMessage().get();
		
		if(refMessage.getEmbeds().size()==0)
			return Util.replyToMessage(message, "You need to reply to a dating minigame.").then();
		
		if(!refMessage.getEmbeds().get(0).getImage().isPresent())
			return Util.replyToMessage(message, "You need to reply to a dating minigame.").then();
		
		return Util.replyToMessage(refMessage, Global.loadingGIF)
			.flatMap(messageRes -> executeSolve(message, refMessage, messageRes))
			.then();
	}

	@SuppressWarnings("deprecation")
	private Mono<? extends Void> executeSolve(Message message, Message refMessage, Message messageRes) {
		try {			
			URL url = URI.create(refMessage.getEmbeds().get(0).getImage().get().getUrl()).toURL();
		    BufferedImage image = ImageIO.read(url);
		    char[][] map = DateSolver.readMap(image);
		    return messageRes.edit(MessageEditSpec.builder().content(DateSolver.showMap(map)).build()).then();
		}catch(Exception e) {
			System.out.println("ERROR");
			e.printStackTrace();
		}
		return messageRes.edit(MessageEditSpec.builder().content("Something went wrong. Tell <@278957461120090113> to fix me.").build()).then();
	}

}
