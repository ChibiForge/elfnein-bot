package com.arracso.ElfneinBot.command.message;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.net.URI;
import java.net.URL;

import javax.imageio.ImageIO;

import com.arracso.ElfneinBot.util.Global;
import com.arracso.ElfneinBot.util.Util;

import discord4j.core.object.entity.Message;
import discord4j.core.spec.MessageEditSpec;
import reactor.core.publisher.Mono;

public class KarutaDyeAnalysisCommand extends MessageCommand {

	public KarutaDyeAnalysisCommand(){
		this.commandNames.add("dyeanalyse");
		this.commandNames.add("dyeanal");
		this.commandNames.add("da");
		commandId = Global.cmdIdDyeAnalysis;
	}
	
	@Override
	public Mono<Void> execute(Message message) {
		// Check refMessage
		if(!message.getReferencedMessage().isPresent())
			return Util.replyToMessage(message, "You need to reply to dye details.").then();
		
		Message refMessage = message.getReferencedMessage().get();
		
		if(refMessage.getEmbeds().size()==0 || !refMessage.getEmbeds().get(0).getTitle().orElse("").equals("Dye Details"))
			return Util.replyToMessage(message, "You need to reply to dye details.").then();
		
		return message.getChannel().block().createMessage(Global.loadingGIF)
			.withMessageReference(message.getData().messageReference())
			.flatMap(messageRes -> executeAnalysis(message, refMessage, messageRes))
			.then();
	}

	@SuppressWarnings("deprecation")
	private Mono<? extends Void> executeAnalysis(Message message, Message refMessage, Message messageRes) {
		try {
			// Delete message
			//message.delete().subscribe();
			
			// Check if its mystic
			Boolean isMystic = refMessage.getEmbeds().get(0).getDescription().get().startsWith("Mystic");
			
			System.out.println(isMystic);
			
			// Get dye image
			URL url = URI.create(refMessage.getEmbeds().get(0).getThumbnail().get().getUrl()).toURL();
		    BufferedImage image = ImageIO.read(url);
			
			// Check glow
			//if(isMystic) {
				Float glow = analyseGlow(image);
				System.out.println(glow);
			//}
			
			
		    
		    System.out.println(image.getHeight());
		    System.out.println(image.getWidth());
		    // Solution
		    
		    
		    return messageRes.edit(MessageEditSpec.builder().content("Impossible to analyse.").build()).then();
		}catch(Exception e) {
			System.out.println("ERROR on DYE ANALYSIS");
			e.printStackTrace();
		}
		return messageRes.edit(MessageEditSpec.builder().content("Something went wrong. Tell <@278957461120090113> to fix me.").build()).then();
	}

	private Float analyseGlow(BufferedImage image) {
		WritableRaster alphaChannel = image.getAlphaRaster();
		
		Float boundary = 0f;
		Float adjusted_boundary = 0f;
		for(int y = 0; y < image.getHeight(); y++) {
			for(int x = 0; x < image.getWidth(); x++) {
				Float alpha = alphaChannel.getSampleFloat(x, y, 0);
				if(50 < alpha && alpha < 255) {
					boundary++;
					adjusted_boundary += alpha/255;
				}
			}
		}
		Float density = adjusted_boundary / boundary;
		
		System.out.println("Boundary: " + normalize(boundary,1582f,4172f));
		System.out.println("Adjusted Boundary: " + adjusted_boundary);
		System.out.println("Density: " + density);
		
		return null;
	}
	
	private Float normalize(Float number, Float min, Float max) {
		return (number - min) / (max - min);
	}

}
