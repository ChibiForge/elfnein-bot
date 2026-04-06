package com.chibiforge.elfnein.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Properties {
	
	@Value("${discord.bot.developer.id}")
	private String developerID;
	
	public String getDeveloperID() {
		return this.developerID;
	}
    
}
