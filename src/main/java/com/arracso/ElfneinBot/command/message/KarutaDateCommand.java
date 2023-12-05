package com.arracso.ElfneinBot.command.message;

import java.awt.image.BufferedImage;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;

import javax.imageio.ImageIO;

import com.arracso.ElfneinBot.util.Global;
import com.arracso.ElfneinBot.util.Locator;
import com.arracso.ElfneinBot.util.Locator.Location;
import com.arracso.ElfneinBot.util.Position;
import com.arracso.ElfneinBot.util.DateSolver.DateSolver;
import com.arracso.ElfneinBot.util.DateSolver.DateStats;
import com.arracso.ElfneinBot.util.DateSolver.DateSolver.Solution;

import discord4j.core.object.entity.Message;
import discord4j.core.spec.MessageEditSpec;
import reactor.core.publisher.Mono;

public class KarutaDateCommand extends MessageCommand {
	
	private String trigger;
	private Location[] triggerLocation;
	private String authorID;
	
	public KarutaDateCommand() {
		this.trigger = Global.kviT;
		this.triggerLocation = Global.kviL;
		this.authorID = Global.KarutaID;
		this.commandId = 1;
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
		return message.getChannel().block().createMessage(Global.loadingGIF).withMessageReference(message.getId())
				.flatMap(messageRes -> executeSolve(message, messageRes)).then();
		
	}

	@SuppressWarnings({ "deprecation" })
	private Mono<? extends Void> executeSolve(Message message, Message messageRes/*,char carDir*/) {
		try {
			// Get direction
			char carDir = '>';
			if(message.getComponents().get(1).getChildren().get(0).getData().disabled().isAbsent()) {
				carDir = '<';
			}
			
			// Check map
			URL url = URI.create(message.getEmbeds().get(0).getImage().get().getUrl()).toURL();
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
