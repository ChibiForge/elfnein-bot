package com.arracso.ElfneinBot.command.message;

import java.awt.image.BufferedImage;
import java.net.URI;
import java.net.URL;

import javax.imageio.ImageIO;

import com.arracso.ElfneinBot.util.DateSolver.DateSolver;

import discord4j.core.object.entity.Message;
import discord4j.core.spec.MessageEditSpec;
import reactor.core.publisher.Mono;

public class KarutaMapDateCommand extends MessageCommand {

	public KarutaMapDateCommand(){
		commandName = "m";
	}
	
	@Override
	public Mono<Void> execute(Message message) {		
		// Check refMessage
		if(!message.getReferencedMessage().isPresent())
			return message.getChannel().block().createMessage("You need to reply to a dating minigame.").withMessageReference(message.getId()).then();
		
		Message refMessage = message.getReferencedMessage().get(); 
		//Util.showEmbed(refMessage);
		
		if(refMessage.getEmbeds().size()==0)
			return message.getChannel().block().createMessage("You need to reply to a dating minigame.").withMessageReference(message.getId()).then();
		
		if(!refMessage.getEmbeds().get(0).getImage().isPresent())
			return message.getChannel().block().createMessage("You need to reply to a dating minigame.").withMessageReference(message.getId()).then();
		
		return message.getChannel().block().createMessage("https://tenor.com/view/cat-meow-loading-loading-paws-gif-5401992")
			.withMessageReference(refMessage.getId())
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
