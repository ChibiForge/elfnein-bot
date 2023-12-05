package com.arracso.ElfneinBot.util;

import java.util.List;

import discord4j.core.object.Embed;
import discord4j.core.object.entity.Message;

public class Locator {
	
	public enum Location {
		CONTENT,
		EMBED,
		TITLE,
		DESCRIPTION,
		FOOTER
	}
	
	public static String get(Message message, Location[] locations) {
		
		// Check if location is content
		if(locations[0] == Location.CONTENT) return message.getContent();
		
		// Check if embeds are empty
		List<Embed> embeds = message.getEmbeds();
		if(embeds.isEmpty()) return "";
		
		// Default: embed
		String locatedString = "";
		int e = 0;
		int i = 1;
		boolean next = true;
		Embed embed = embeds.get(e);
		while(next && i < locations.length) {
			switch(locations[i]) {
				case EMBED:
					e++;
					if(e<embeds.size()) embed = embeds.get(e);
					else next = false;
					break;
				case TITLE:
					if(embed.getTitle().isPresent()) locatedString = embed.getTitle().get();
					next = false;
					break;
				case DESCRIPTION:
					if(embed.getDescription().isPresent()) locatedString = embed.getDescription().get();
					next = false;
					break;
				case FOOTER:
					//TODO
					break;
				default:
					//TODO
					break;
			}					
			i++;
		}
		return locatedString;
	}

}
