package com.chibiforge.elfnein.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import discord4j.core.object.Embed;
import discord4j.core.object.Embed.Image;
import discord4j.core.object.component.Container;
import discord4j.core.object.entity.Message;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.discordjson.json.MessageReferenceData;
import discord4j.rest.util.AllowedMentions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
    
    public static Mono<Message> replyToMessage(Message message, String content) {
    	MessageReferenceData ref = MessageReferenceData.builder().messageId(message.getId().asLong()).build();
    	return message.getChannel().flatMap(channel -> channel.createMessage(content).withMessageReference(ref));
    }
    
    public static Mono<Message> replyToMessage(Message message, EmbedCreateSpec content) {
    	MessageReferenceData ref = MessageReferenceData.builder().messageId(message.getId().asLong()).build();
    	return message.getChannel().flatMap(channel -> channel.createMessage(content).withMessageReference(ref));
    }
    
    public static Mono<Message> replyToMessage(Message message, Container... content) {
    	MessageReferenceData ref = MessageReferenceData.builder().messageId(message.getId().asLong()).build();
    	return message.getChannel().flatMap(channel -> channel.createMessage().withFlags(Message.Flag.IS_COMPONENTS_V2).withComponents(content).withMessageReference(ref));
    }
    
    public static Mono<Message> replyToMessageSilent(Message message, Container... content) {
    	MessageReferenceData ref = MessageReferenceData.builder().messageId(message.getId().asLong()).build();
    	return message.getChannel().flatMap(channel -> channel.createMessage().withFlags(Message.Flag.IS_COMPONENTS_V2).withComponents(content).withMessageReference(ref).withAllowedMentions(AllowedMentions.suppressAll()));
    }
    
    public static String parseId(String par) {
		String id = par;
		if(par.startsWith("<@")) id = par.split("@")[1].split(">")[0];
		return id;
	}
    
    public static void showReply(Message message) {
    	System.out.println("###################");
		System.out.println("#### NEW REPLY ####");
		System.out.println("###################");
		
		message.getMessageReference().ifPresent(messageReference -> {
			// You can get id and channel id of the referenced message
			System.out.println("Ref Channel ID: " + messageReference.getChannelId().toString());
			System.out.println("Ref Message ID: " + messageReference.getMessageId().toString());
			System.out.println(message.getClient().getMessageById(messageReference.getChannelId(), messageReference.getMessageId().get()).block().getContent());
		});
		
		message.getComponents().forEach(component ->{
			System.out.println("component"); // Theres no components
		});
		
		message.getReferencedMessage().ifPresent(refMessage ->{
			System.out.println("Referenced message"); // This only shows if the bot has access to the referenced message
			System.out.println(refMessage.getContent());
		});
		
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
				embed.getFields().forEach(field -> {
					System.out.println(field.getName());
					System.out.println(field.getValue());
				});
				
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
    
    public static Map<String, String> parseButtonMetadata(String metadataStr) {
		Map<String,String> metadata = new HashMap<String,String>();
		for(String kv : metadataStr.split("&")) {
			String [] p = kv.split("=", 2);
			if(p.length == 2) metadata.put(p[0],p[1]);
		}
		return metadata;
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

	public static String trimCommand(String content, String commandName) {
		return substring(content, content.length()-content.toLowerCase().replaceFirst(".*?"+commandName, "").trim().length(),content.length());
	}
	
}
