package com.arracso.ElfneinBot.util;

import java.util.ArrayList;
import java.util.List;

import discord4j.core.object.Embed;
import discord4j.core.object.Embed.Image;
import discord4j.core.object.entity.Message;
import reactor.core.publisher.Flux;

public class Util {
	
	private static String[] botNames = {"Elfnein","<@"+Global.ElfneinID+">"};
	
	///////////////
	// Functions //
	///////////////
	
	/**
	 * Checks if the bot is mentioned on the message
	 * @param message
	 * @return
	 */
    public static Boolean mentionsBot(Message message) {
    	// Look for mentions
    	for(String name:botNames)
    		if(message.getContent().toLowerCase().contains(name)) return true;
    	// Look for author replied
    	if(message.getReferencedMessage().isPresent()) {
    		Message refMessage = message.getReferencedMessage().get();
    		if(refMessage.getAuthor().isPresent() && refMessage.getAuthor().get().isBot())
    			return refMessage.getAuthor().get().getId().asString().equals(Global.ElfneinID);
    	}
    	
    	return false;
    }
    
    public static Flux<Message> getReplies(Message refMessage) {
    	return refMessage.getChannel().block().getMessagesAfter(refMessage.getId())
    	.filter(message -> message.getReferencedMessage().isPresent())
    	.filter(message -> message.getReferencedMessage().get().getId().toString().equals(refMessage.getId().toString()));
    }
    
    public static void showEmbed(Message message) {
		System.out.println("#################################");
		System.out.println("#### NEW MESSAGE FROM KARUTA ####");
		System.out.println("#################################");
		List<Embed> embeds = message.getEmbeds();
		embeds.forEach(embed -> {
			System.out.println("=============");
			System.out.println("=== embed ===");
			System.out.println("=============");
			if(embed.getTitle().isPresent()) {
				System.out.println("-----------");
				System.out.println("-- Title --");
				System.out.println("-----------");
				System.out.println(embed.getTitle().get());
			}
			if(embed.getDescription().isPresent()) {
				System.out.println("-----------------");
				System.out.println("-- Description --");
				System.out.println("-----------------");
				System.out.println(embed.getDescription().get());				
			}
			if(!embed.getFields().isEmpty()) {
				System.out.println("------------");
				System.out.println("-- Fields --");
				System.out.println("------------");
				embed.getFields().forEach(field -> System.out.print(field.getName()));
			}
			if(embed.getFooter().isPresent()) {
				System.out.println("------------");
				System.out.println("-- Footer --");
				System.out.println("------------");
				Embed footerEmbed = embed.getFooter().get().getEmbed();
				if(footerEmbed.getTitle().isPresent()) {
					System.out.println("...........");
					System.out.println(".. Title ..");
					System.out.println("...........");
					System.out.println(footerEmbed.getDescription().get());
				}
				if(footerEmbed.getDescription().isPresent()) {
					System.out.println(".................");
					System.out.println(".. Description ..");
					System.out.println(".................");
					System.out.println(footerEmbed.getDescription().get());
				}
			}
			if(embed.getImage().isPresent()) {
				System.out.println("-----------");
				System.out.println("-- Image --");
				System.out.println("-----------");
				Image image = embed.getImage().get();
				System.out.println(""+image.getWidth()+"x"+image.getHeight());
			}
		});
		
    }

	//////////////////
	// Karuta Utils //
	//////////////////

	public static List<String> getCards(String text) {
		List<String> cards = new ArrayList<String>();
		String[] lines = text.split("\n");
		for(int i = 2; i<lines.length;i++) {
			String[] words = lines[i].split(" ");				
			int j = 1;
			while(!words[j].startsWith("**`") && j < words.length) j++;
			if(j != words.length) {
				String card = substring(words[j],3,-3);
				while(Character.getNumericValue(card.charAt(0)) == -1) card = card.substring(1);
				cards.add(card);
			}
		}
		return cards;
	}	
	
	public static String substring(String str, int i, int f) {
		if(f<0) f = str.length()+f;
		return str.substring(i,f);
	}
	
}
