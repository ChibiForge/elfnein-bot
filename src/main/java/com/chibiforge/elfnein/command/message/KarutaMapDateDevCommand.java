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

public class KarutaMapDateDevCommand extends MessageCommand {

	public KarutaMapDateDevCommand(){
		commandNames.add("test map");
		commandId = 0;
	}
	
	@Override
	public Mono<Void> execute(Message message) {		
		// Check refMessage
		if(!message.getReferencedMessage().isPresent())
			return Util.replyToMessage(message, "You need to reply to a map image.").then();
		
		Message refMessage = message.getReferencedMessage().get();
		
		System.out.println(refMessage.getAttachments().get(0).getUrl());
		
		return Util.replyToMessage(refMessage, Global.loadingGIF)
			.flatMap(messageRes -> executeSolve(message, refMessage, messageRes))
			.then();
	}

	@SuppressWarnings("deprecation")
	private Mono<? extends Void> executeSolve(Message message, Message refMessage, Message messageRes) {
		try {			
			URL url = URI.create(refMessage.getAttachments().get(0).getUrl()).toURL();
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
