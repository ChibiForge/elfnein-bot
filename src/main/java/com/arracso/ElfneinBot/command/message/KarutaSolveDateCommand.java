package com.arracso.ElfneinBot.command.message;

import java.awt.image.BufferedImage;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;

import javax.imageio.ImageIO;

import com.arracso.ElfneinBot.util.Position;
import com.arracso.ElfneinBot.util.DateSolver.*;
import com.arracso.ElfneinBot.util.DateSolver.DateSolver.*;

import discord4j.core.object.entity.Message;
import discord4j.core.spec.MessageEditSpec;
import reactor.core.publisher.Mono;

public class KarutaSolveDateCommand extends MessageCommand {

	public KarutaSolveDateCommand(){
		commandName = "solve";
	}
	
	@Override
	public Mono<Void> execute(Message message) {		
		// Check refMessage
		if(!message.getReferencedMessage().isPresent())
			return message.getChannel().block().createMessage("You need to reply to a dating minigame.").withMessageReference(message.getId()).then();
		
		Message refMessage = message.getReferencedMessage().get(); 
		
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
			// Get direction
			char carDir = '>';
			if(refMessage.getComponents().get(1).getChildren().get(0).getData().disabled().isAbsent()) {
				carDir = '<';
			}
			
			// Delete message
			message.delete().subscribe();
			
			// Check map
			URL url = URI.create(refMessage.getEmbeds().get(0).getImage().get().getUrl()).toURL();
		    BufferedImage image = ImageIO.read(url);
		    char[][] map = DateSolver.readMap(image);
		    
		    // Solution
			Solution sol = DateSolver.solve(map,new Position(5,14),carDir,new DateStats(),new HashMap<Position,Integer>());
			if(sol != null) return messageRes.edit(MessageEditSpec.builder().content(sol.getActions()+ "`" + sol.getRealPoints() + "`").build()).then();
			else return messageRes.edit(MessageEditSpec.builder().content("Impossible Board :(").build()).then();
		}catch(Exception e) {
			System.out.println("ERROR");
			e.printStackTrace();
		}
		return messageRes.edit(MessageEditSpec.builder().content("Something went wrong. Tell <@278957461120090113> to fix me.").build()).then();
	}

}
